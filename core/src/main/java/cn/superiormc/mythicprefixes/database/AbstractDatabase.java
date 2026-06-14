package cn.superiormc.mythicprefixes.database;

import cn.superiormc.mythicprefixes.objects.ObjectCache;
import cn.superiormc.mythicprefixes.objects.DynamicPrefixRequest;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractDatabase {

    public void onInit() {
        // Empty...
    }

    public void onClose() {
        // Empty...
    }

    public abstract void checkData(ObjectCache cache);

    public abstract void updateData(ObjectCache cache, boolean quitServer);

    public CompletableFuture<Void> saveDynamicPrefixRequest(Player player, String prefixID, String value) {
        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<Collection<DynamicPrefixRequest>> getPendingDynamicPrefixRequests() {
        return CompletableFuture.completedFuture(Collections.emptyList());
    }

    public CompletableFuture<Integer> getPendingDynamicPrefixRequestAmount() {
        return CompletableFuture.completedFuture(0);
    }

    public CompletableFuture<Boolean> approveDynamicPrefixRequest(String playerUUID, String prefixID) {
        return CompletableFuture.completedFuture(false);
    }

    public CompletableFuture<Boolean> denyDynamicPrefixRequest(String playerUUID, String prefixID) {
        return CompletableFuture.completedFuture(false);
    }

    public CompletableFuture<Void> clearDynamicPrefixValue(String playerUUID, String prefixID) {
        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<Void> saveDynamicPrefixValue(String playerUUID, String prefixID, String value) {
        return CompletableFuture.completedFuture(null);
    }

    public void updateDataOnDisable(ObjectCache cache, boolean disable) {
        updateData(cache, true);
    }
}
