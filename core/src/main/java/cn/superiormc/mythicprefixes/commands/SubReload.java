package cn.superiormc.mythicprefixes.commands;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.manager.LanguageManager;
import cn.superiormc.mythicprefixes.manager.TaskManager;
import cn.superiormc.mythicprefixes.objects.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SubReload extends AbstractCommand {


    public SubReload() {
        this.id = "reload";
        this.requiredPermission =  "mythicprefixes." + id;
        this.onlyInGame = false;
        this.requiredArgLength = new Integer[]{1};
    }

    /* Usage:

    /prefix reload

     */
    @Override
    public void executeCommandInGame(String[] args, Player player) {
        MythicPrefixes.instance.reloadConfig();
        TaskManager.taskManager.cancelTask();
        for (Player p : Bukkit.getOnlinePlayers()) {
            CacheManager.cacheManager.getPlayerCache(player).runAllPrefixEndActions();
            CacheManager.cacheManager.savePlayerCacheOnDisable(p);
        }
        new ConfigManager();
        new LanguageManager();
        new CacheManager();
        new TaskManager();
        for (Player p : Bukkit.getOnlinePlayers()) {
            CacheManager.cacheManager.addPlayerCache(p);
            CacheManager.cacheManager.getPlayerCache(p).setAsFinished();
            CacheManager.cacheManager.loadPlayerCache(p);
        }
        LanguageManager.languageManager.sendStringText(player, "plugin.reloaded");
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        MythicPrefixes.instance.reloadConfig();
        TaskManager.taskManager.cancelTask();
        for (Player p : Bukkit.getOnlinePlayers()) {
            CacheManager.cacheManager.getPlayerCache(p).runAllPrefixEndActions();
            CacheManager.cacheManager.savePlayerCacheOnDisable(p);
        }
        new ConfigManager();
        new LanguageManager();
        new CacheManager();
        new TaskManager();
        for (Player p : Bukkit.getOnlinePlayers()) {
            CacheManager.cacheManager.addPlayerCache(p);
            CacheManager.cacheManager.getPlayerCache(p).setAsFinished();
            CacheManager.cacheManager.loadPlayerCache(p);
        }
        LanguageManager.languageManager.sendStringText("plugin.reloaded");
    }
}
