package cn.superiormc.mythicprefixes.methods;

import cn.superiormc.mythicprefixes.commands.SubDynamicPrefix;
import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.manager.CommandManager;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.manager.LanguageManager;
import cn.superiormc.mythicprefixes.objects.AbstractCommand;
import cn.superiormc.mythicprefixes.objects.DynamicPrefixRequest;
import cn.superiormc.mythicprefixes.objects.ObjectCache;
import cn.superiormc.mythicprefixes.objects.buttons.ObjectPrefix;
import cn.superiormc.mythicprefixes.utils.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class DynamicPrefixes {

    private static final Map<UUID, ObjectPrefix> editingDynamicPrefixes = new HashMap<>();

    public static void openDynamicPrefixEditor(Player player, ObjectPrefix prefix) {
        if (!prefix.isDynamicPrefix()) {
            return;
        }
        if (prefix.isConditionNotMeet(CacheManager.cacheManager.getPlayerCache(player))) {
            LanguageManager.languageManager.sendStringText(player, "dynamic-prefix.condition-not-meet", "prefix", prefix.getId());
            return;
        }
        player.closeInventory();
        editingDynamicPrefixes.put(player.getUniqueId(), prefix);
        LanguageManager.languageManager.sendStringText(player, "dynamic-prefix.input",
                "prefix", prefix.getId(),
                "current", prefix.getDisplayValue(player));
    }

    public static boolean handleDynamicPrefixChat(Player player, String message) {
        ObjectPrefix prefix = editingDynamicPrefixes.remove(player.getUniqueId());
        if (prefix == null) {
            return false;
        }
        if (message.equalsIgnoreCase(LanguageManager.languageManager.getStringText(player, "override-lang.prompt.cancel-keyword"))) {
            LanguageManager.languageManager.sendStringText(player, "dynamic-prefix.cancelled");
            return true;
        }
        submitDynamicPrefix(player, prefix, message);
        return true;
    }

    public static boolean hasDynamicPrefixEditor(Player player) {
        return editingDynamicPrefixes.containsKey(player.getUniqueId());
    }

    public static void submitDynamicPrefix(Player player, ObjectPrefix prefix, String value) {
        String error = validateDynamicPrefixValue(value);
        if (error != null) {
            LanguageManager.languageManager.sendStringText(player, error);
            return;
        }
        ObjectCache cache = CacheManager.cacheManager.getPlayerCache(player);
        String approvedValue = cache.getApprovedDynamicPrefixValue(prefix.getId());
        cache.setPendingDynamicPrefixValue(prefix.getId(), value);
        cache.setDynamicPrefixValue(prefix.getId(), ObjectCache.markDynamicPrefixPending(approvedValue, value));
        CacheManager.cacheManager.database.saveDynamicPrefixRequest(player, prefix.getId(), value).thenRun(() -> {
            cacheDynamicPrefixRequest(player, prefix.getId(), value);
            notifyDynamicPrefixAdmins();
        });
        LanguageManager.languageManager.sendStringText(player, "dynamic-prefix.submitted",
                "prefix", prefix.getId(),
                "value", value);
    }

    private static String validateDynamicPrefixValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "dynamic-prefix.empty";
        }
        if (value.contains(";;")) {
            return "dynamic-prefix.empty";
        }
        int min = ConfigManager.configManager.getInt("dynamic-prefix.min-length", 1);
        int max = ConfigManager.configManager.getInt("dynamic-prefix.max-length", 32);
        if (value.length() < min || value.length() > max) {
            return "dynamic-prefix.length";
        }
        String lower = value.toLowerCase(Locale.ROOT);
        for (String word : ConfigManager.configManager.config.getStringList("dynamic-prefix.sensitive-words")) {
            if (!word.isEmpty() && lower.contains(word.toLowerCase(Locale.ROOT))) {
                return "dynamic-prefix.sensitive-word";
            }
        }
        return null;
    }

    public static void notifyDynamicPrefixAdmins() {
        CacheManager.cacheManager.database.getPendingDynamicPrefixRequestAmount().thenAccept(amount -> {
            if (amount <= 0) {
                return;
            }
            SchedulerUtil.runSync(() -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.hasPermission("mythicprefixes.dynamicprefix.review") || player.hasPermission("mythicprefixes.admin")) {
                        LanguageManager.languageManager.sendStringText(player, "dynamic-prefix.admin-notify", "amount", String.valueOf(amount));
                    }
                }
            });
        });
    }

    private static void cacheDynamicPrefixRequest(Player player, String prefixID, String value) {
        if (CommandManager.commandManager == null) {
            return;
        }
        AbstractCommand command = CommandManager.commandManager.getSubCommandsMap().get("dynamicprefix");
        if (command instanceof SubDynamicPrefix) {
            SubDynamicPrefix dynamicPrefixCommand = (SubDynamicPrefix) command;
            dynamicPrefixCommand.cachePendingRequest(new DynamicPrefixRequest(
                    player.getUniqueId().toString(),
                    player.getName(),
                    prefixID,
                    value
            ));
        }
    }
}
