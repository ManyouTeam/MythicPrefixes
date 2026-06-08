package cn.superiormc.mythicprefixes.manager;

import cn.superiormc.mythicprefixes.objects.ObjectCache;
import cn.superiormc.mythicprefixes.objects.buttons.ObjectPrefix;
import cn.superiormc.mythicprefixes.utils.SchedulerUtil;
import cn.superiormc.mythicprefixes.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {

    public static TaskManager taskManager;

    private SchedulerUtil saveTask;

    private SchedulerUtil conditionCheckTask;

    private final Map<Long, SchedulerUtil> circleActionTasks = new HashMap<>();

    public TaskManager() {
        taskManager = this;
        if (ConfigManager.configManager.getBoolean("auto-save.enabled")) {
            initSaveTasks();
        }
        initConditionCheckTasks();
        initCircleActionTasks();
    }

    public void initSaveTasks() {
        saveTask = SchedulerUtil.runTaskTimer(() -> {
            if (!ConfigManager.configManager.getBoolean("auto-save.hide-message")) {
                TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fAuto saving data...");
                TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fIf this lead to server TPS drop, " +
                        "you should consider disable auto save feature at config.yml!");
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (CacheManager.cacheManager.getPlayerCache(player) != null) {
                    CacheManager.cacheManager.getPlayerCache(player).shutPlayerCache(false);
                }
            }
        }, 180L, ConfigManager.configManager.config.getLong("auto-save.period-tick", 600));
    }

    public void initConditionCheckTasks() {
        conditionCheckTask = SchedulerUtil.runTaskTimer(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (CacheManager.cacheManager.getPlayerCache(player) != null) {
                    CacheManager.cacheManager.getPlayerCache(player).checkCondition();
                }
            }
        }, 20L, 20L);
    }

    public void initCircleActionTasks() {
        Map<Long, List<ObjectPrefix>> prefixesByPeriod = new HashMap<>();
        for (ObjectPrefix prefix : ConfigManager.configManager.getPrefixes()) {
            if (!prefix.requiresCircleTask()) {
                continue;
            }
            prefixesByPeriod.computeIfAbsent(prefix.getCircleActionPeriodTick(), key -> new ArrayList<>()).add(prefix);
        }
        for (Map.Entry<Long, List<ObjectPrefix>> entry : prefixesByPeriod.entrySet()) {
            long periodTick = entry.getKey();
            List<ObjectPrefix> prefixes = entry.getValue();
            SchedulerUtil task = SchedulerUtil.runTaskTimer(() -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    ObjectCache cache = CacheManager.cacheManager.getPlayerCache(player);
                    Collection<ObjectPrefix> activePrefixes = cache.getActivePrefixes();
                    for (ObjectPrefix prefix : prefixes) {
                        if (activePrefixes.contains(prefix)) {
                            prefix.runCircleAction(player);
                        }
                    }
                }
            }, 1L, periodTick);
            circleActionTasks.put(periodTick, task);
        }
    }

    public void cancelTask() {
        if (saveTask != null) {
            saveTask.cancel();
        }
        if (conditionCheckTask != null) {
            conditionCheckTask.cancel();
        }
        for (SchedulerUtil circleActionTask : circleActionTasks.values()) {
            circleActionTask.cancel();
        }
        circleActionTasks.clear();
    }
}
