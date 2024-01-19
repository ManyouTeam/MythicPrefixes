package cn.superiormc.mythicprefixes.manager;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class TaskManager {

    private BukkitTask saveTask;

    public TaskManager() {
        if (ConfigManager.configManager.getBoolean("auto-save.enabled")) {
            initSaveTasks();
        }
    }

    public void initSaveTasks() {
        saveTask = new BukkitRunnable() {

            @Override
            public void run() {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicPrefixes] §fAuto saving data...");
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicPrefixes] §fIf this lead to server TPS drop, " +
                        "you should consider disable auto save feature at config.yml!");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (CacheManager.cacheManager.getPlayerCache(player) != null) {
                        CacheManager.cacheManager.getPlayerCache(player).shutPlayerCache(false);
                    }
                }
            }

        }.runTaskTimer(MythicPrefixes.instance, 180L, ConfigManager.configManager.config.getLong("auto-save.period-tick", 600));
    }

    public BukkitTask getSaveTask() {
        return saveTask;
    }
}
