package cn.superiormc.mythicprefixes.commands;

import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.manager.DatabaseManager;
import cn.superiormc.mythicprefixes.manager.LanguageManager;
import cn.superiormc.mythicprefixes.objects.AbstractCommand;
import cn.superiormc.mythicprefixes.objects.DynamicPrefixRequest;
import cn.superiormc.mythicprefixes.objects.ObjectCache;
import cn.superiormc.mythicprefixes.objects.buttons.ObjectPrefix;
import cn.superiormc.mythicprefixes.utils.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SubDynamicPrefix extends AbstractCommand {

    private volatile List<DynamicPrefixRequest> pendingRequestCache = Collections.emptyList();

    private volatile boolean refreshingTabCache = false;

    public SubDynamicPrefix() {
        this.id = "dynamicprefix";
        this.requiredPermission = "mythicprefixes.dynamicprefix.review";
        this.onlyInGame = false;
        this.requiredArgLength = new Integer[]{2, 4};
        refreshPendingRequestCache();
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        execute(player, args);
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        execute(null, args);
    }

    private void execute(Player sender, String[] args) {
        if (args[1].equalsIgnoreCase("list")) {
            DatabaseManager.databaseManager.database.getPendingDynamicPrefixRequests().thenAccept(requests ->
                    SchedulerUtil.runSync(() -> sendList(sender, requests)));
            return;
        }
        if (args.length != 4) {
            LanguageManager.languageManager.sendStringText(sender, "error.args");
            return;
        }
        String playerUUID = args[2];
        String prefixID = args[3];
        if (!isValidUUID(playerUUID)) {
            LanguageManager.languageManager.sendStringText(sender, "error.args");
            return;
        }
        if (args[1].equalsIgnoreCase("approve")) {
            DatabaseManager.databaseManager.database.approveDynamicPrefixRequest(playerUUID, prefixID).thenAccept(success ->
                    SchedulerUtil.runSync(() -> afterHandle(sender, playerUUID, prefixID, success, true)));
        } else if (args[1].equalsIgnoreCase("deny")) {
            DatabaseManager.databaseManager.database.denyDynamicPrefixRequest(playerUUID, prefixID).thenAccept(success ->
                    SchedulerUtil.runSync(() -> afterHandle(sender, playerUUID, prefixID, success, false)));
        } else {
            LanguageManager.languageManager.sendStringText(sender, "error.args");
        }
    }

    private void sendList(Player sender, Iterable<DynamicPrefixRequest> requests) {
        int amount = 0;
        List<DynamicPrefixRequest> cache = new ArrayList<>();
        for (DynamicPrefixRequest request : requests) {
            cache.add(request);
            amount++;
            LanguageManager.languageManager.sendStringText(sender, "dynamic-prefix.list-format",
                    "uuid", request.getPlayerUUID(),
                    "player", request.getPlayerName(),
                    "prefix", request.getPrefixID(),
                    "value", request.getValue());
        }
        if (amount == 0) {
            LanguageManager.languageManager.sendStringText(sender, "dynamic-prefix.list-empty");
        }
        pendingRequestCache = cache;
    }

    private boolean isValidUUID(String value) {
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private void afterHandle(Player sender, String playerUUID, String prefixID, boolean success, boolean approve) {
        if (!success) {
            LanguageManager.languageManager.sendStringText(sender, "dynamic-prefix.request-not-found");
            return;
        }
        Player target = Bukkit.getPlayer(UUID.fromString(playerUUID));
        if (target != null) {
            ObjectCache cache = CacheManager.cacheManager.getPlayerCache(target);
            if (cache == null) {
                LanguageManager.languageManager.sendStringText(sender, "error.player-not-found", "player", target.getName());
                return;
            }
            String pending = cache.getPendingDynamicPrefixValue(prefixID);
            if (pending == null) {
                pending = cache.getCleanDynamicPrefixValue(prefixID);
            }
            if (approve) {
                cache.setDynamicPrefixValue(prefixID, pending);
                DatabaseManager.databaseManager.database.saveDynamicPrefixValue(playerUUID, prefixID, pending);
            } else {
                String approvedValue = cache.getApprovedDynamicPrefixValue(prefixID);
                if (approvedValue == null || approvedValue.isEmpty()) {
                    cache.clearDynamicPrefixValue(prefixID);
                    DatabaseManager.databaseManager.database.clearDynamicPrefixValue(playerUUID, prefixID);
                } else {
                    cache.setDynamicPrefixValue(prefixID, approvedValue);
                    DatabaseManager.databaseManager.database.saveDynamicPrefixValue(playerUUID, prefixID, approvedValue);
                }
            }
            cache.clearPendingDynamicPrefixValue(prefixID);
            ObjectPrefix prefix = ConfigManager.configManager.getPrefix(prefixID);
            LanguageManager.languageManager.sendStringText(target,
                    approve ? "dynamic-prefix.approved-player" : "dynamic-prefix.denied-player",
                    "prefix", prefix == null ? prefixID : prefix.getId());
        }
        LanguageManager.languageManager.sendStringText(sender,
                approve ? "dynamic-prefix.approved-admin" : "dynamic-prefix.denied-admin",
                "uuid", playerUUID,
                "prefix", prefixID);
        refreshPendingRequestCache();
    }

    @Override
    public List<String> getTabResult(int length) {
        List<String> result = new ArrayList<>();
        if (length == 2) {
            result.add("list");
            result.add("approve");
            result.add("deny");
        }
        return result;
    }

    @Override
    public List<String> getTabResult(String[] args) {
        if (args.length == 2) {
            return getTabResult(args.length);
        }
        if (!isHandleAction(args)) {
            return new ArrayList<>();
        }
        refreshPendingRequestCache();
        if (args.length == 3) {
            return getPendingRequestUUIDs();
        }
        if (args.length == 4) {
            return getPendingRequestPrefixes(args[2]);
        }
        return new ArrayList<>();
    }

    private boolean isHandleAction(String[] args) {
        return args.length >= 2 && (args[1].equalsIgnoreCase("approve") || args[1].equalsIgnoreCase("deny"));
    }

    public void refreshPendingRequestCache() {
        if (refreshingTabCache || CacheManager.cacheManager == null || DatabaseManager.databaseManager.database == null) {
            return;
        }
        refreshingTabCache = true;
        DatabaseManager.databaseManager.database.getPendingDynamicPrefixRequests().thenAccept(requests -> {
            Collection<DynamicPrefixRequest> safeRequests = requests == null ? Collections.emptyList() : requests;
            pendingRequestCache = new ArrayList<>(safeRequests);
        }).whenComplete((ignored, throwable) -> refreshingTabCache = false);
    }

    public void cachePendingRequest(DynamicPrefixRequest request) {
        List<DynamicPrefixRequest> result = new ArrayList<>(pendingRequestCache);
        result.removeIf(existing -> existing.getPlayerUUID().equalsIgnoreCase(request.getPlayerUUID())
                && existing.getPrefixID().equalsIgnoreCase(request.getPrefixID()));
        result.add(request);
        pendingRequestCache = result;
    }

    private List<String> getPendingRequestUUIDs() {
        Set<String> result = new LinkedHashSet<>();
        for (DynamicPrefixRequest request : pendingRequestCache) {
            result.add(request.getPlayerUUID());
        }
        return new ArrayList<>(result);
    }

    private List<String> getPendingRequestPrefixes(String playerUUID) {
        List<String> result = new ArrayList<>();
        for (DynamicPrefixRequest request : pendingRequestCache) {
            if (request.getPlayerUUID().equalsIgnoreCase(playerUUID)) {
                result.add(request.getPrefixID());
            }
        }
        return result;
    }
}
