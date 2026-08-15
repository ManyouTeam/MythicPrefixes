package cn.superiormc.mythicprefixes.database;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.objects.ObjectCache;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DatabaseExecutor {

    private static final int PLAYER_DATA_QUEUE_CAPACITY = 10_000;

    private static final int DATABASE_TASK_QUEUE_CAPACITY = 20_000;

    private static final long WARNING_INTERVAL_MILLIS = 30_000L;

    private static final Map<String, Runnable> pendingSaves = new ConcurrentHashMap<>();

    private static final Set<String> scheduledSaves = ConcurrentHashMap.newKeySet();

    private static final AtomicLong lastQueueWarning = new AtomicLong();

    private static TrackingExecutor playerDataExecutor;

    private static TrackingExecutor databaseTaskExecutor;

    private static boolean acceptingTasks;

    private static TrackingExecutor createPlayerDataExecutor() {
        int threadCount = databaseThreadCount();
        return new TrackingExecutor(
                threadCount,
                threadCount,
                new LinkedBlockingQueue<>(PLAYER_DATA_QUEUE_CAPACITY),
                "MythicPrefixes-PlayerData"
        );
    }

    private static TrackingExecutor createDatabaseTaskExecutor() {
        return new TrackingExecutor(
                1,
                1,
                new LinkedBlockingQueue<>(DATABASE_TASK_QUEUE_CAPACITY),
                "MythicPrefixes-DatabaseTask"
        );
    }

    public static synchronized void start() {
        if (playerDataExecutor == null || playerDataExecutor.isShutdown()) {
            playerDataExecutor = createPlayerDataExecutor();
        }
        if (databaseTaskExecutor == null || databaseTaskExecutor.isShutdown()) {
            databaseTaskExecutor = createDatabaseTaskExecutor();
        }
        acceptingTasks = true;
    }

    public static synchronized void executePlayerSave(String storageId, Runnable saveTask) {
        if (storageId == null || storageId.isEmpty() || saveTask == null) {
            return;
        }
        ensureAccepting();
        pendingSaves.put(storageId, saveTask);
        if (!scheduledSaves.add(storageId)) {
            return;
        }
        try {
            playerDataExecutor.execute(() -> drainPlayerSave(storageId));
        } catch (RejectedExecutionException exception) {
            scheduledSaves.remove(storageId);
            pendingSaves.remove(storageId, saveTask);
            throw exception;
        }
    }

    public static CompletableFuture<Void> executeDatabaseTask(Runnable task) {
        return supplyDatabaseTask(() -> {
            task.run();
            return null;
        });
    }

    public static <T> CompletableFuture<T> supplyDatabaseTask(Supplier<T> task) {
        CompletableFuture<T> future = new CompletableFuture<>();
        synchronized (DatabaseExecutor.class) {
            ensureAccepting();
            databaseTaskExecutor.execute(() -> {
                try {
                    future.complete(task.get());
                } catch (Throwable throwable) {
                    future.completeExceptionally(throwable);
                }
            });
        }
        return future;
    }

    public static void executeCacheLoad(ObjectCache cache, Consumer<ObjectCache> loadTask) {
        if (cache == null || loadTask == null) {
            return;
        }
        WeakReference<ObjectCache> cacheReference = new WeakReference<>(cache);
        executeDatabaseTask(() -> {
            ObjectCache currentCache = cacheReference.get();
            if (currentCache != null) {
                loadTask.accept(currentCache);
            }
        });
    }

    private static void drainPlayerSave(String storageId) {
        try {
            Runnable saveTask;
            while ((saveTask = pendingSaves.remove(storageId)) != null) {
                saveTask.run();
            }
        } finally {
            scheduledSaves.remove(storageId);
            synchronized (DatabaseExecutor.class) {
                if (pendingSaves.containsKey(storageId) && scheduledSaves.add(storageId)
                        && playerDataExecutor != null && !playerDataExecutor.isShutdown()) {
                    playerDataExecutor.execute(() -> drainPlayerSave(storageId));
                }
            }
        }
    }

    public static synchronized void quiesce() {
        acceptingTasks = false;
    }

    public static synchronized void resume() {
        if (playerDataExecutor == null || playerDataExecutor.isShutdown()
                || databaseTaskExecutor == null || databaseTaskExecutor.isShutdown()) {
            throw new RejectedExecutionException("MythicPrefixes database executor is not running");
        }
        acceptingTasks = true;
    }

    public static void await() {
        TrackingExecutor currentPlayerDataExecutor;
        TrackingExecutor currentDatabaseTaskExecutor;
        synchronized (DatabaseExecutor.class) {
            currentPlayerDataExecutor = playerDataExecutor;
            currentDatabaseTaskExecutor = databaseTaskExecutor;
        }
        if (currentPlayerDataExecutor != null) {
            currentPlayerDataExecutor.awaitTasks();
        }
        if (currentDatabaseTaskExecutor != null) {
            currentDatabaseTaskExecutor.awaitTasks();
        }
    }

    public static synchronized void shutdown() {
        acceptingTasks = false;
        if (playerDataExecutor != null) {
            playerDataExecutor.shutdownNow();
            playerDataExecutor = null;
        }
        if (databaseTaskExecutor != null) {
            databaseTaskExecutor.shutdownNow();
            databaseTaskExecutor = null;
        }
        pendingSaves.clear();
        scheduledSaves.clear();
    }

    private static int databaseThreadCount() {
        return Math.max(2, Math.min(4, Runtime.getRuntime().availableProcessors() / 2));
    }

    private static void ensureAccepting() {
        if (!acceptingTasks) {
            throw new RejectedExecutionException("MythicPrefixes database executor is not accepting tasks");
        }
    }

    private static class TrackingExecutor extends ThreadPoolExecutor {

        private final Object taskLock = new Object();

        private int pendingTasks;

        private TrackingExecutor(int corePoolSize,
                                 int maximumPoolSize,
                                 BlockingQueue<Runnable> workQueue,
                                 String threadName) {
            super(corePoolSize, maximumPoolSize, 0L, TimeUnit.MILLISECONDS, workQueue,
                    runnable -> new Thread(runnable, threadName),
                    (runnable, executor) -> {
                        if (executor.isShutdown()) {
                            throw new RejectedExecutionException("Database executor is shut down");
                        }
                        warnQueueFull(threadName);
                        runnable.run();
                    });
        }

        @Override
        public void execute(Runnable command) {
            synchronized (taskLock) {
                pendingTasks++;
            }
            try {
                super.execute(() -> {
                    try {
                        command.run();
                    } finally {
                        synchronized (taskLock) {
                            pendingTasks--;
                            if (pendingTasks == 0) {
                                taskLock.notifyAll();
                            }
                        }
                    }
                });
            } catch (RejectedExecutionException exception) {
                synchronized (taskLock) {
                    pendingTasks--;
                    if (pendingTasks == 0) {
                        taskLock.notifyAll();
                    }
                }
                throw exception;
            }
        }

        private void awaitTasks() {
            synchronized (taskLock) {
                while (pendingTasks > 0) {
                    try {
                        taskLock.wait();
                    } catch (InterruptedException exception) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }
    }

    private static void warnQueueFull(String executorName) {
        long now = System.currentTimeMillis();
        long previous = lastQueueWarning.get();
        if (now - previous < WARNING_INTERVAL_MILLIS || !lastQueueWarning.compareAndSet(previous, now)) {
            return;
        }
        String message = executorName + " queue is full; applying caller backpressure to prevent data loss.";
        if (MythicPrefixes.instance != null) {
            MythicPrefixes.instance.getLogger().warning(message);
        } else {
            System.err.println("[MythicPrefixes] " + message);
        }
    }
}
