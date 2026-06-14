package cn.superiormc.mythicprefixes.methods;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.database.DatabaseExecutor;
import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.manager.LanguageManager;
import cn.superiormc.mythicprefixes.manager.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadPlugin {

    public static void reload(CommandSender sender) {
        reload(sender, false);
    }

    public static void reload(CommandSender sender, boolean reloadDatabase) {
        MythicPrefixes.instance.reloadConfig();
        TaskManager.taskManager.cancelTask();
        if (reloadDatabase) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                CacheManager.cacheManager.getPlayerCache(p).runAllPrefixEndActions();
                CacheManager.cacheManager.savePlayerCacheOnDisable(p, false);
            }
            DatabaseExecutor.await();
            CacheManager.cacheManager.database.onClose();
            CacheManager.cacheManager.shutdown();
        }
        new ConfigManager();
        new LanguageManager();
        if (reloadDatabase) {
            new CacheManager();
        }
        new TaskManager();
        if (reloadDatabase) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                CacheManager.cacheManager.addPlayerCache(p);
                CacheManager.cacheManager.getPlayerCache(p).setAsFinished();
                CacheManager.cacheManager.loadPlayerCache(p);
            }
        }
        LanguageManager.languageManager.sendStringText(sender, "plugin.reloaded");
    }
}
