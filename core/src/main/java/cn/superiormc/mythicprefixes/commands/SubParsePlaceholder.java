package cn.superiormc.mythicprefixes.commands;

import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.manager.LanguageManager;
import cn.superiormc.mythicprefixes.objects.AbstractCommand;
import cn.superiormc.mythicprefixes.objects.ObjectCache;
import cn.superiormc.mythicprefixes.objects.ObjectDisplayPlaceholder;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SubParsePlaceholder extends AbstractCommand {


    public SubParsePlaceholder() {
        this.id = "parseplaceholder";
        this.requiredPermission =  "mythicprefixes." + id;
        this.onlyInGame = true;
        this.requiredArgLength = new Integer[]{2};
    }

    /* Usage:

    /prefix parseplaceholder <placeholderID>

     */
    @Override
    public void executeCommandInGame(String[] args, Player player) {
        ObjectDisplayPlaceholder displayPlaceholder = ConfigManager.configManager.getDisplayPlaceholder(args[1]);
        if (displayPlaceholder == null) {
            LanguageManager.languageManager.sendStringText(player, "error.placeholder-not-found", "placeholder", args[1]);
            return;
        }
        ObjectCache cache = CacheManager.cacheManager.getPlayerCache(player);
        LanguageManager.languageManager.sendStringText(player, "parse-placeholder", "placeholder", args[1], "value",
                displayPlaceholder.getDisplayText(cache));
    }

    @Override
    public List<String> getTabResult(int length) {
        List<String> tempVal1 = new ArrayList<>();
        switch (length) {
            case 2:
                for (ObjectDisplayPlaceholder placeholder : ConfigManager.configManager.placeholderConfigs.values()) {
                    tempVal1.add(placeholder.getId());
                }
                break;
        }
        return tempVal1;
    }
}
