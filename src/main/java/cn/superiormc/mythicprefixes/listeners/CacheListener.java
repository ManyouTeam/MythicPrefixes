package cn.superiormc.mythicprefixes.listeners;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.manager.CacheManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CacheListener implements Listener {

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Bukkit.getScheduler().runTaskLater(MythicPrefixes.instance, () -> {
            CacheManager.cacheManager.addPlayerCache(event.getPlayer());
        }, 5L);
    }

    @EventHandler
    public void onExit(PlayerQuitEvent event) {
        CacheManager.cacheManager.savePlayerCache(event.getPlayer());
    }
}
