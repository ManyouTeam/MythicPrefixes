package cn.superiormc.mythicprefixes.commands;

import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.manager.LanguageManager;
import cn.superiormc.mythicprefixes.objects.ObjectCache;
import cn.superiormc.mythicprefixes.objects.AbstractCommand;
import cn.superiormc.mythicprefixes.objects.buttons.ObjectPrefix;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SubSetPrefix extends AbstractCommand {


    public SubSetPrefix() {
        this.id = "setprefix";
        this.requiredPermission =  "mythicprefixes." + id;
        this.onlyInGame = false;
        this.requiredArgLength = new Integer[]{2, 3};
    }

    /* Usage:

    /prefix setprefix <prefixIDs>
    /prefix setprefix <playerIDs> <prefixID>

     */
    @Override
    public void executeCommandInGame(String[] args, Player player) {
        Player whoWillAdd = Bukkit.getPlayer(args[1]);
        if (whoWillAdd == null) {
            if (args.length == 3) {
                whoWillAdd = player;
            } else {
                LanguageManager.languageManager.sendStringText(player, "error.player-not-found", "player", args[1]);
                return;
            }
        }
        ObjectCache playerCache = CacheManager.cacheManager.getPlayerCache(whoWillAdd);
        playerCache.removeAllActivePrefix();
        for (String prefixKey : args[args.length - 1].split(";;")) {
            ObjectPrefix tempVal2 = ConfigManager.configManager.getPrefix(prefixKey);
            if (tempVal2 == null) {
                LanguageManager.languageManager.sendStringText(player, "error.prefix-not-found",
                        "prefix", prefixKey);
                continue;
            }
            playerCache.addActivePrefix(tempVal2);
        }
        LanguageManager.languageManager.sendStringText(player, "success-set-prefix",
                "player", whoWillAdd.getName(),
                "prefix", args[args.length - 1]);
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        Player whoWillAdd = Bukkit.getPlayer(args[1]);
        if (whoWillAdd == null) {
            LanguageManager.languageManager.sendStringText("error.player-not-found", "player", args[1]);
            return;
        }
        ObjectCache playerCache = CacheManager.cacheManager.getPlayerCache(whoWillAdd);
        playerCache.removeAllActivePrefix();
        for (String prefixKey : args[2].split(";;")) {
            ObjectPrefix tempVal2 = ConfigManager.configManager.getPrefix(prefixKey);
            if (tempVal2 == null) {
                LanguageManager.languageManager.sendStringText("error.prefix-not-found",
                        "prefix", prefixKey);
                continue;
            }
            playerCache.addActivePrefix(tempVal2);
        }
        LanguageManager.languageManager.sendStringText("success-set-prefix",
                "player", args[1],
                "prefix", args[2]);
    }

    @Override
    public List<String> getTabResult(int length) {
        List<String> tempVal1 = new ArrayList<>();
        switch (length) {
            case 2:
                for (Player player : Bukkit.getOnlinePlayers()) {
                    tempVal1.add(player.getName());
                }
                break;
            case 3:
                for (ObjectPrefix prefix : ConfigManager.configManager.getPrefixes()) {
                    tempVal1.add(prefix.getId());
                }
                break;
        }
        return tempVal1;
    }
}
