package cn.superiormc.mythicprefixes.manager;

import cn.superiormc.mythicprefixes.utils.SchedulerUtil;
import cn.superiormc.mythicprefixes.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TaskManager {

    public static TaskManager taskManager;

    private SchedulerUtil saveTask;

    private SchedulerUtil conditionCheckTask;

    public TaskManager() {
        taskManager = this;
        if (ConfigManager.configManager.getBoolean("auto-save.enabled")) {
            initSaveTasks();
        }
        initConditionCheckTasks();
    }

    public void initSaveTasks() {
        saveTask = SchedulerUtil.runTaskTimer(() -> {
            if (!ConfigManager.configManager.getBoolean("auto-save.hide-message")) {
                Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §fAuto saving data...");
                Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §fIf this lead to server TPS drop, " +

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

    public void cancelTask() {
        if (saveTask != null) {
            saveTask.cancel();
        }
        if (conditionCheckTask != null) {
            conditionCheckTask.cancel();
        }
    }
}
