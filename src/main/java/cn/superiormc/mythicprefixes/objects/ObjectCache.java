package cn.superiormc.mythicprefixes.objects;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.api.MythicPrefixesAPI;
import cn.superiormc.mythicprefixes.database.SQLDatabase;
import cn.superiormc.mythicprefixes.database.YamlDatabase;
import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.objects.buttons.ObjectPrefix;
import cn.superiormc.mythicprefixes.utils.SchedulerUtil;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.TreeSet;

public class ObjectCache {

    private final Player player;

    public Collection<ObjectPrefix> prefixCaches = new TreeSet<>();

    private boolean finishLoad;

    public ObjectCache(Player player) {
        this.player = player;
    }

    public void initPlayerCache() {
        SchedulerUtil.runTaskAsynchronously(() -> {
            if (ConfigManager.configManager.getBoolean("database.enabled")) {
                SQLDatabase.checkData(this);
            }
            else {
                YamlDatabase.checkData(this);
            }
        });
    }

    public void shutPlayerCache(boolean quitServer) {
        SchedulerUtil.runTaskAsynchronously(() -> {
            if (ConfigManager.configManager.getBoolean("database.enabled")) {
                SQLDatabase.updateData(this, quitServer);
            }
            else {
                YamlDatabase.updateData(this, quitServer);
            }
        });
    }

    public void shutPlayerCacheOnDisable() {
        if (ConfigManager.configManager.getBoolean("database.enabled")) {
            SQLDatabase.updateDataNoAsync(this);
        }
        else {
            YamlDatabase.updateData(this, true);
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void setActivePrefixes(String values) {
        for (String prefixKey : values.split(";;")) {
            ObjectPrefix tempVal2 = ConfigManager.configManager.getPrefix(prefixKey);
            if (tempVal2 == null) {
                continue;
            }
            addActivePrefix(tempVal2);
        }
    }

    public void addActivePrefix(ObjectPrefix prefix) {
        if (prefix.getPrefixStatus(this) != PrefixStatus.CAN_USE) {
            return;
        }
        prefix.runStartAction(player);
        prefixCaches.add(prefix);
    }

    public void removeActivePrefix(ObjectPrefix prefix) {
        prefix.runEndAction(player);
        prefixCaches.remove(prefix);
    }

    public void removeAllActivePrefix() {
        for (ObjectPrefix prefix : MythicPrefixesAPI.getActivedPrefixes(player)) {
            removeActivePrefix(prefix);
        }
    }

    public Collection<ObjectPrefix> getActivePrefixes() {
        return new TreeSet<>(CacheManager.cacheManager.getPlayerCache(player).prefixCaches);
    }

    public String getActivePrefixesID() {
        int i = 0;
        StringBuilder tempVal2 = new StringBuilder();
        for (ObjectPrefix tempVal1 : prefixCaches) {
            if (i > 0) {
                tempVal2.append(";;");
            }
            tempVal2.append(tempVal1.getId());
            i++;
        }
        return tempVal2.toString();
    }

    public void checkCondition() {
        if (!isFinishLoad()) {
            return;
        }
        for (ObjectPrefix prefix : MythicPrefixesAPI.getActivedPrefixes(player)) {
            if (!prefix.getCondition().getAllBoolean(player)) {
                removeActivePrefix(prefix);
            }
        }
    }

    public void setAsFinished() {
        finishLoad = true;
    }

    public boolean isFinishLoad() {
        if (!ConfigManager.configManager.getBoolean("cache.bypass-condition-when-loading")) {
            return true;
        }
        return finishLoad;
    }
}
