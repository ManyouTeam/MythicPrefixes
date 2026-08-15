package cn.superiormc.mythicprefixes.database;

import cn.superiormc.mythicprefixes.objects.ObjectCache;

import java.util.Map;
import java.util.UUID;

public record PlayerDataSnapshot(
        UUID playerUUID,
        String playerName,
        String activePrefixes,
        Map<String, String> dynamicPrefixValues
) {

    public PlayerDataSnapshot {
        dynamicPrefixValues = Map.copyOf(dynamicPrefixValues);
    }

    public static PlayerDataSnapshot from(ObjectCache cache) {
        return new PlayerDataSnapshot(
                cache.getPlayer().getUniqueId(),
                cache.getPlayer().getName(),
                cache.getActivePrefixesID(),
                cache.getDynamicPrefixValues()
        );
    }

    public String storageId() {
        return playerUUID.toString();
    }
}
