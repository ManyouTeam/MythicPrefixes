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

import java.util.HashMap;
import java.util.Map;

public class CacheManager {

    public static CacheManager cacheManager;

    private final Map<Player, ObjectCache> playerCacheMap = new HashMap<>();

    private final Map<Player, SchedulerUtil> delayCacheMap = new HashMap<>();

    public AbstractDatabase database;

    private boolean isStoppingServer;

    public CacheManager() {
        cacheManager = this;
        isStoppingServer = false;
        if (ConfigManager.configManager.getBoolean("database.enabled")) {
            database = new SQLDatabase();
        } else {
            database = new YamlDatabase();
        }
    }

    public void addPlayerCache(Player player) {
        if (delayCacheMap.containsKey(player)) {
            delayCacheMap.get(player).cancel();
        }
        if (!playerCacheMap.containsKey(player)) {
            playerCacheMap.put(player, new ObjectCache(player));
        }
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

    public void removePlayerCache(Player player) {
        if (ConfigManager.configManager.getLong("cache.remove-delay", -1L) > 0) {
            SchedulerUtil tempVal1 = SchedulerUtil.runTaskLater(new BukkitRunnable() {
                @Override
                public void run() {
                    playerCacheMap.get(player).removeAllActivePrefix(false);
                    playerCacheMap.remove(player);
                    delayCacheMap.remove(player);
                }
            }, ConfigManager.configManager.getLong("cache.remove-delay", 60L));
            delayCacheMap.put(player, tempVal1);
        } else {
            playerCacheMap.get(player).removeAllActivePrefix(false);
            playerCacheMap.remove(player);
        }
    }

    public void savePlayerCacheOnExit(Player player) {
        if (playerCacheMap.get(player) == null) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cError: Can not save player data: " + player.getName() + "!");
            return;
        }
        playerCacheMap.get(player).shutPlayerCache(true);
    }

    public void savePlayerCacheOnDisable(Player player, boolean disable) {
        if (playerCacheMap.get(player) == null) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cError: Can not save player data: " + player.getName() + "!");
            return;
        }
        playerCacheMap.get(player).shutPlayerCacheOnDisable(disable);
    }

    public void setStoppingServer() {
        isStoppingServer = true;
    }

    public ObjectCache getPlayerCache(Player player) {
        ObjectCache playerCache = playerCacheMap.get(player);
        if (playerCache == null) {
            CacheManager.cacheManager.addPlayerCache(player);
            playerCache = CacheManager.cacheManager.getPlayerCache(player);
        }
        return playerCache;
    }

}
