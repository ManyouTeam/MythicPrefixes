package cn.superiormc.mythicprefixes.manager;

import cn.superiormc.mythicprefixes.objects.ObjectCache;
import cn.superiormc.mythicprefixes.database.SQLDatabase;
import cn.superiormc.mythicprefixes.objects.effect.ObjectAuraSkillsEffect;
import cn.superiormc.mythicprefixes.utils.CommonUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CacheManager {

    public static CacheManager cacheManager;

    private final Map<Player, ObjectCache> playerCacheMap = new HashMap<>();

    public CacheManager() {
        cacheManager = this;
        if (ConfigManager.configManager.getBoolean("database.enabled")) {
            SQLDatabase.closeSQL();
            SQLDatabase.initSQL();
        }
    }

    public void addPlayerCache(Player player) {
        if (!playerCacheMap.containsKey(player)) {
            playerCacheMap.put(player, new ObjectCache(player));
        }
    }

    public void loadPlayerCache(Player player) {
        ObjectCache cache = getPlayerCache(player);
        cache.removeAllActivePrefix();
        if (CommonUtil.checkPluginLoad("AuraSkills")) {
            ObjectAuraSkillsEffect.removePlayerStat(player, 1);
        }
        cache.initPlayerCache();
    }

    public void removePlayerCache(Player player) {
        playerCacheMap.get(player).removeAllActivePrefix();
        playerCacheMap.remove(player);
    }

    public void savePlayerCacheOnExit(Player player) {
        if (playerCacheMap.get(player) == null) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicPrefixes] §cError: Can not save player data: " + player.getName() + "!");
            return;
        }
        playerCacheMap.get(player).shutPlayerCache(true);
    }

    public void savePlayerCacheOnDisable(Player player) {
        if (playerCacheMap.get(player) == null) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicPrefixes] §cError: Can not save player data: " + player.getName() + "!");
            return;
        }
        playerCacheMap.get(player).shutPlayerCacheOnDisable();
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
