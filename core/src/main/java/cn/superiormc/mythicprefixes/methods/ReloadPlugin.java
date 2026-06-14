package cn.superiormc.mythicprefixes.methods;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.manager.LanguageManager;
import cn.superiormc.mythicprefixes.manager.TaskManager;
import cn.superiormc.mythicprefixes.objects.ObjectCache;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadPlugin {

    public static void reload(CommandSender sender) {
        MythicPrefixes.instance.reloadConfig();
        TaskManager.taskManager.cancelTask();
        for (Player p : Bukkit.getOnlinePlayers()) {
            ObjectCache cache = CacheManager.cacheManager.getPlayerCache(p);
            if (cache != null) {
                cache.runAllPrefixEndActions();
            }
            CacheManager.cacheManager.savePlayerCacheOnDisable(p, false);
        }
        CacheManager.cacheManager.shutdown();
        new ConfigManager();
        new LanguageManager();
        new CacheManager();
        new TaskManager();
        LanguageManager.languageManager.sendStringText(sender, "plugin.reloaded");
    }
}
