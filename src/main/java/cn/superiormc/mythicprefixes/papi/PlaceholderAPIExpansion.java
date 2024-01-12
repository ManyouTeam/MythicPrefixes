package cn.superiormc.mythicprefixes.papi;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.manager.LanguageManager;
import cn.superiormc.mythicprefixes.objects.ObjectDisplayPlaceholder;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
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
        if (player == null) {
            return null;
        }
        String[] args = params.split("_");
        if (args.length == 0) {
            return null;
        }
        if (args[0].equals("amount")) {
            return String.valueOf(CacheManager.cacheManager.getPlayerCache(player).getActivePrefixes().size());
        }
        ObjectDisplayPlaceholder displayPlaceholder = ConfigManager.configManager.getDisplayPlaceholder(args[0]);
        if (displayPlaceholder == null) {
            return LanguageManager.languageManager.getStringText("placeholderapi.unknown-display-placeholder");
        }
        return displayPlaceholder.getDisplayText(player);
    }
}
