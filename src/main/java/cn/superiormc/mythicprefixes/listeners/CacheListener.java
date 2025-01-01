package cn.superiormc.mythicprefixes.listeners;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.utils.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CacheListener implements Listener {

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        SchedulerUtil.runTaskLater(() -> {
            CacheManager.cacheManager.addPlayerCache(event.getPlayer());
            if (ConfigManager.configManager.getString("cache.load-mode").equals("LOGIN")) {
                CacheManager.cacheManager.loadPlayerCache(event.getPlayer());
            }
        }, 5L);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!ConfigManager.configManager.getString("cache.load-mode").equals("LOGIN")) {
            CacheManager.cacheManager.loadPlayerCache(event.getPlayer());
        }
        CacheManager.cacheManager.getPlayerCache(event.getPlayer()).setAsFinished();
    }

    @EventHandler
    public void onExit(PlayerQuitEvent event) {
        CacheManager.cacheManager.savePlayerCache(event.getPlayer());
    }
}
