package cn.superiormc.mythicprefixes.listeners;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.methods.DynamicPrefixes;
import cn.superiormc.mythicprefixes.objects.ObjectCache;
import cn.superiormc.mythicprefixes.utils.PacketInventoryUtil;
import cn.superiormc.mythicprefixes.utils.SchedulerUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CacheListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        CacheManager.cacheManager.addPlayerCache(event.getPlayer());
        DynamicPrefixes.notifyDynamicPrefixAdmins();
        SchedulerUtil.runTaskLater(() -> {
            if (!event.getPlayer().isOnline()) {
                return;
            }
            CacheManager.cacheManager.loadPlayerCache(event.getPlayer());
        }, ConfigManager.configManager.getLong("cache.load-delay", 7L));
    }

    @EventHandler
    public void onExit(PlayerQuitEvent event) {
        DynamicPrefixes.closeDynamicPrefixEditor(event.getPlayer());
        if (MythicPrefixes.usePacketEvents && PacketInventoryUtil.packetInventoryUtil != null) {
            PacketInventoryUtil.packetInventoryUtil.clear(event.getPlayer());
        }
        ObjectCache cache = CacheManager.cacheManager.getPlayerCache(event.getPlayer());
        if (cache != null) {
            cache.runAllPrefixEndActions();
        }
        CacheManager.cacheManager.savePlayerCacheOnExit(event.getPlayer());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (DynamicPrefixes.hasDynamicPrefixEditor(event.getPlayer())) {
            event.setCancelled(true);
            SchedulerUtil.runSync(event.getPlayer(), () -> DynamicPrefixes.handleDynamicPrefixChat(event.getPlayer(), event.getMessage()));
        }
    }
}
