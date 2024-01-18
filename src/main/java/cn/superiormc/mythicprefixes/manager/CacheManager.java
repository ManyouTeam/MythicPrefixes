package cn.superiormc.mythicprefixes.manager;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.objects.ObjectCache;
import cn.superiormc.mythicprefixes.database.SQLDatabase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CacheManager {

    public static CacheManager cacheManager;

    private Map<Player, ObjectCache> playerCacheMap = new HashMap<>();

    public CacheManager() {
        cacheManager = this;
        if (ConfigManager.configManager.getBoolean("database.enabled")) {
            SQLDatabase.closeSQL();
            SQLDatabase.initSQL();
        }
    }

    public void reload() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            CacheManager.cacheManager.savePlayerCacheOnDisable(player);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            CacheManager.cacheManager.addPlayerCache(player);
        }
    }

    public void addPlayerCache(Player player) {
        playerCacheMap.put(player, new ObjectCache(player));
        playerCacheMap.get(player).removeAllActivePrefix();
        playerCacheMap.get(player).initPlayerCache();
    }

    public void removePlayerCache(Player player) {
        playerCacheMap.get(player).removeAllActivePrefix();
        playerCacheMap.remove(player);
    }

    public void savePlayerCache(Player player) {
        if (playerCacheMap.get(player) == null) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicPrefixes] §cError: Can not save player data: " + player.getName() + "!");
            return;
        }
        playerCacheMap.get(player).shutPlayerCache();
    }

    public void savePlayerCacheOnDisable(Player player) {
        if (playerCacheMap.get(player) == null) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicPrefixes] §cError: Can not save player data: " + player.getName() + "!");
        }
        playerCacheMap.get(player).shutPlayerCacheOnDisable();
    }

    public ObjectCache getPlayerCache(Player player) {
        ObjectCache playerCache = playerCacheMap.get(player);
        if (playerCache == null) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicPrefixes] §cError: Can not get " + player.getName() +
                    " cache, regenerate a new cache object for the player.");
            CacheManager.cacheManager.addPlayerCache(player);
            playerCache = CacheManager.cacheManager.getPlayerCache(player);
        }
        return playerCache;
    }

}
