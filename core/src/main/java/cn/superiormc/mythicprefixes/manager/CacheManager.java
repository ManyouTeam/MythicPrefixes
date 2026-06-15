package cn.superiormc.mythicprefixes.manager;

import cn.superiormc.mythicprefixes.objects.ObjectCache;
import cn.superiormc.mythicprefixes.objects.effect.ObjectAuraSkillsEffect;
import cn.superiormc.mythicprefixes.utils.CommonUtil;
import cn.superiormc.mythicprefixes.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CacheManager {

    public static CacheManager cacheManager;

    private final Map<UUID, ObjectCache> playerCacheMap = new ConcurrentHashMap<>();

    public CacheManager() {
        cacheManager = this;
        reloadCache();
    }

    public void reloadCache() {
        shutdown();
        for (Player p : Bukkit.getOnlinePlayers()) {
            addPlayerCache(p);
            loadPlayerCache(p);
        }
    }

    public void addPlayerCache(Player player) {
        if (player != null) {
            UUID playerUUID = player.getUniqueId();
            playerCacheMap.put(playerUUID, new ObjectCache(player));
        }
    }

    public void loadPlayerCache(Player player) {
        ObjectCache cache = playerCacheMap.get(player.getUniqueId());
        if (cache != null) {
            cache.runAllPrefixEndActions();
            cache.removeAllActivePrefix(false);
            if (CommonUtil.checkPluginLoad("AuraSkills")) {
                ObjectAuraSkillsEffect.removePlayerStat(player, 1);
            }
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fLoading player data: " + player.getName() + "...");
            cache.initPlayerCache();
        }
    }

    public void removePlayerCache(ObjectCache cache) {
        if (cache != null && cache.getPlayer() != null) {
            if (playerCacheMap.remove(cache.getPlayer().getUniqueId(), cache)) {
                cache.removeAllActivePrefix(false);
            }
        }
    }

    public void savePlayerCacheOnExit(Player player) {
        ObjectCache cache = playerCacheMap.get(player.getUniqueId());
        if (cache == null) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cError: Can not save player data: " + player.getName() + "!");
            return;
        }
        if (cache.canNotModify()) {
            removePlayerCache(cache);
            cache.close();
        } else {
            cache.shutPlayerCache(true);
        }
    }

    public void savePlayerCacheOnDisable(Player player, boolean disable) {
        ObjectCache cache = playerCacheMap.get(player.getUniqueId());
        if (cache == null) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cError: Can not save player data: " + player.getName() + "!");
            return;
        }
        if (cache.canNotModify()) {
            removePlayerCache(cache);
            cache.close();
        } else {
            cache.shutPlayerCacheOnDisable(disable);
        }
    }

    public void shutdown() {
        playerCacheMap.values().forEach(ObjectCache::close);
        playerCacheMap.clear();
    }

    @Nullable
    public ObjectCache getPlayerCache(Player player) {
        ObjectCache cache = playerCacheMap.get(player.getUniqueId());
        if (cache == null || cache.canNotModify()) {
            return null;
        }
        return cache;
    }

}
