package cn.superiormc.mythicprefixes.papi;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.api.MythicPrefixesAPI;
import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.manager.LanguageManager;
import cn.superiormc.mythicprefixes.objects.ObjectCache;
import cn.superiormc.mythicprefixes.objects.ObjectDisplayPlaceholder;
import cn.superiormc.mythicprefixes.objects.buttons.ObjectPrefix;
import cn.superiormc.mythicprefixes.utils.TextUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIExpansion extends PlaceholderExpansion {

    public static PlaceholderAPIExpansion papi = null;

    private final MythicPrefixes plugin;
    @Override
    public boolean canRegister() {
        return true;
    }

    public PlaceholderAPIExpansion(MythicPrefixes plugin) {
        this.plugin = plugin;
    }

    @NotNull
    @Override
    public String getAuthor() {
        return "PQguanfang";
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "mythicprefixes";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, String params) {
        Player player = offlinePlayer.getPlayer();
        if (player == null || params == null || params.isEmpty()) {
            return null;
        }

        ObjectCache cache = CacheManager.cacheManager.getPlayerCache(player);
        if (cache == null) {
            return null;
        }

        if (params.equals("amount")) {
            return String.valueOf(cache.getActivePrefixes().size());

        } else if (params.startsWith("status_")) {
            String prefixId = params.substring("status_".length());
            ObjectPrefix prefix = ConfigManager.configManager.getPrefix(prefixId);
            if (prefix == null) {
                return LanguageManager.languageManager.getStringText("placeholderapi.unknown-prefix");
            }
            return String.valueOf(prefix.getPrefixStatus(cache));

        } else if (params.startsWith("prefix_")) {
            // 截掉"prefix_"部分
            String sub = params.substring("prefix_".length());
            int splitIndex = sub.lastIndexOf("_");

            // 有 displayPlaceholderId
            if (splitIndex != -1) {
                String prefixId = sub.substring(0, splitIndex);
                String displayId = sub.substring(splitIndex + 1);

                ObjectPrefix prefix = ConfigManager.configManager.getPrefix(prefixId);
                if (prefix == null) {
                    return LanguageManager.languageManager.getStringText("placeholderapi.unknown-prefix");
                }

                ObjectDisplayPlaceholder displayPlaceholder = ConfigManager.configManager.getDisplayPlaceholder(displayId);
                if (displayPlaceholder == null) {
                    return LanguageManager.languageManager.getStringText("placeholderapi.unknown-display-placeholder");
                }
                return displayPlaceholder.getDisplayText(cache, prefix);
            } else if (!MythicPrefixes.freeVersion) {
                // 只有prefixId
                ObjectPrefix prefix = ConfigManager.configManager.getPrefix(sub);
                if (prefix == null) {
                    return LanguageManager.languageManager.getStringText("placeholderapi.unknown-prefix");
                }
                return TextUtil.parse(prefix.getDisplayValue(player));
            }

        } else if (params.startsWith("no_") && !MythicPrefixes.freeVersion) {
            String sub = params.substring("no_".length());
            int lastUnderscore = sub.lastIndexOf("_");

            if (lastUnderscore == -1) {
                return null;
            }

            String displayId = sub.substring(0, lastUnderscore);
            String indexStr = sub.substring(lastUnderscore + 1);
            int index;

            try {
                index = Integer.parseInt(indexStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;
            }

            ObjectDisplayPlaceholder displayPlaceholder = ConfigManager.configManager.getDisplayPlaceholder(displayId);
            if (displayPlaceholder == null)
                return LanguageManager.languageManager.getStringText("placeholderapi.unknown-display-placeholder");

            return displayPlaceholder.getNoPrefixDisplayText(cache, index);

        } else if (params.equals("max")) {
            return String.valueOf(MythicPrefixesAPI.getMaxPrefixesAmount(player, null));
        } else {
            // 默认：整个params作为 displayPlaceholderId
            ObjectDisplayPlaceholder displayPlaceholder = ConfigManager.configManager.getDisplayPlaceholder(params);
            if (displayPlaceholder == null)
                return LanguageManager.languageManager.getStringText("placeholderapi.unknown-display-placeholder");

            return displayPlaceholder.getDisplayText(cache);
        }
        return null;
    }
}
