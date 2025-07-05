package cn.superiormc.mythicprefixes.utils;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class SchedulerUtil {

    private BukkitTask bukkitTask;

    private ScheduledTask scheduledTask;

    public SchedulerUtil(BukkitTask bukkitTask) {
        this.bukkitTask = bukkitTask;
    }

    public SchedulerUtil(ScheduledTask scheduledTask) {
        this.scheduledTask = scheduledTask;
    }

    public void cancel() {
        if (MythicPrefixes.isFolia) {
            scheduledTask.cancel();
        } else {
            bukkitTask.cancel();
        }
    }

    /**
     * delay at least 1 ticks for Folia
     */
    private static long ensureValidDelay(long delayTicks) {
        return delayTicks <= 0 ? 1 : delayTicks;
    }

    /**
     * SchedulerUtil instance for Folia
     */
    private static SchedulerUtil createFoliaScheduler(ScheduledTask task) {
        return new SchedulerUtil(task);
    }

    /**
     * SchedulerUtil instance for Bukkit
     */
    private static SchedulerUtil createBukkitScheduler(BukkitTask task) {
        return new SchedulerUtil(task);
    }

    // 在主线程上运行任务
    public static void runSync(Runnable task) {
        if (MythicPrefixes.isFolia) {
            Bukkit.getGlobalRegionScheduler().execute(MythicPrefixes.instance, task);
        } else {
            Bukkit.getScheduler().runTask(MythicPrefixes.instance, task);
        }
    }

    // 在异步线程上运行任务
    public static void runTaskAsynchronously(Runnable task) {
        if (MythicPrefixes.isFolia) {
            task.run();
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(MythicPrefixes.instance, task);
        }
    }

    // 延迟执行任务
    public static SchedulerUtil runTaskLater(Runnable task, long delayTicks) {
        if (MythicPrefixes.isFolia) {
            long validDelay = ensureValidDelay(delayTicks);
            return createFoliaScheduler(Bukkit.getGlobalRegionScheduler().runDelayed(
                MythicPrefixes.instance, scheduledTask -> task.run(), validDelay));
        } else {
            return createBukkitScheduler(Bukkit.getScheduler().runTaskLater(
                MythicPrefixes.instance, task, delayTicks));
        }
    }

    // 定时循环任务
    public static SchedulerUtil runTaskTimer(Runnable task, long delayTicks, long periodTicks) {
        if (MythicPrefixes.isFolia) {
            long validDelay = ensureValidDelay(delayTicks);
            return createFoliaScheduler(Bukkit.getGlobalRegionScheduler().runAtFixedRate(
                MythicPrefixes.instance, scheduledTask -> task.run(), validDelay, periodTicks));
        } else {
            return createBukkitScheduler(Bukkit.getScheduler().runTaskTimer(
                MythicPrefixes.instance, task, delayTicks, periodTicks));
        }
    }

    // 延迟执行任务
    public static SchedulerUtil runTaskLaterAsynchronously(Runnable task, long delayTicks) {
        if (MythicPrefixes.isFolia) {
            long validDelay = ensureValidDelay(delayTicks);
            return createFoliaScheduler(Bukkit.getGlobalRegionScheduler().runDelayed(
                MythicPrefixes.instance, scheduledTask -> task.run(), validDelay));
        } else {
            return createBukkitScheduler(Bukkit.getScheduler().runTaskLaterAsynchronously(
                MythicPrefixes.instance, task, delayTicks));
        }
    }
}
