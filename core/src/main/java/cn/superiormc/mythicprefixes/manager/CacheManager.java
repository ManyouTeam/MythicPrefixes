package cn.superiormc.mythicprefixes.manager;

import cn.superiormc.mythicprefixes.database.AbstractDatabase;
import cn.superiormc.mythicprefixes.database.YamlDatabase;
import cn.superiormc.mythicprefixes.objects.ObjectCache;
import cn.superiormc.mythicprefixes.database.SQLDatabase;
import cn.superiormc.mythicprefixes.objects.effect.ObjectAuraSkillsEffect;
import cn.superiormc.mythicprefixes.utils.CommonUtil;
import cn.superiormc.mythicprefixes.utils.SchedulerUtil;
import cn.superiormc.mythicprefixes.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CacheManager {

    public static CacheManager cacheManager;

    private final Map<UUID, ObjectCache> playerCacheMap = new ConcurrentHashMap<>();

    private final Map<UUID, SchedulerUtil> delayCacheMap = new ConcurrentHashMap<>();

    public AbstractDatabase database;

    public CacheManager() {
        cacheManager = this;
        if (ConfigManager.configManager.getBoolean("database.enabled")) {
            database = new SQLDatabase();
        } else {
            database = new YamlDatabase();
        }
        database.onInit();
    }

    public void addPlayerCache(Player player) {
        UUID playerUUID = player.getUniqueId();
        SchedulerUtil delayedRemoval = delayCacheMap.remove(playerUUID);
        if (delayedRemoval != null) {
            delayedRemoval.cancel();
        }
        playerCacheMap.computeIfAbsent(playerUUID, ignored -> new ObjectCache(player));
    }

    public void loadPlayerCache(Player player) {
        ObjectCache cache = getPlayerCache(player);
        cache.runAllPrefixEndActions();
        cache.removeAllActivePrefix(false);
        if (CommonUtil.checkPluginLoad("AuraSkills")) {
            ObjectAuraSkillsEffect.removePlayerStat(player, 1);
        }
        cache.initPlayerCache();
    }

    public void removePlayerCache(ObjectCache cache) {
        Player player = cache.getPlayer();
        UUID playerUUID = player.getUniqueId();
        if (ConfigManager.configManager.getLong("cache.remove-delay", -1L) > 0) {
            SchedulerUtil[] taskRef = new SchedulerUtil[1];
            taskRef[0] = SchedulerUtil.runTaskLater(new BukkitRunnable() {
                @Override
                public void run() {
                    if (playerCacheMap.remove(playerUUID, cache)) {
                        cache.removeAllActivePrefix(false);
                    }
                    delayCacheMap.remove(playerUUID, taskRef[0]);
                }
            }, ConfigManager.configManager.getLong("cache.remove-delay", 60L));
            SchedulerUtil oldTask = delayCacheMap.put(playerUUID, taskRef[0]);
            if (oldTask != null) {
                oldTask.cancel();
            }
        } else {
            if (playerCacheMap.remove(playerUUID, cache)) {
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
        cache.shutPlayerCache(true);
    }

    public void savePlayerCacheOnDisable(Player player, boolean disable) {
        ObjectCache cache = playerCacheMap.get(player.getUniqueId());
        if (cache == null) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cError: Can not save player data: " + player.getName() + "!");
            return;
        }
        cache.shutPlayerCacheOnDisable(disable);
    }

    public void shutdown() {
        for (SchedulerUtil delayedRemoval : delayCacheMap.values()) {
            delayedRemoval.cancel();
        }
        delayCacheMap.clear();
        playerCacheMap.clear();
    }

    public ObjectCache getPlayerCache(Player player) {
        ObjectCache playerCache = playerCacheMap.get(player.getUniqueId());
        if (playerCache == null) {
            CacheManager.cacheManager.addPlayerCache(player);
            playerCache = CacheManager.cacheManager.getPlayerCache(player);
        }
        return playerCache;
    }

}
