package cn.superiormc.mythicprefixes.commands;

import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.manager.LanguageManager;
import cn.superiormc.mythicprefixes.objects.ObjectCache;
import cn.superiormc.mythicprefixes.objects.ObjectCommand;
import cn.superiormc.mythicprefixes.objects.buttons.ObjectPrefix;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SubViewUsingPrefix extends ObjectCommand {


    public SubViewUsingPrefix() {
        this.id = "viewusingprefix";
        this.requiredPermission =  "mythicprefixes." + id;
        this.onlyInGame = false;
        this.requiredArgLength = new Integer[]{1, 2};
    }

    /* Usage:

    /prefix viewusingprefix
    /prefix viewusingprefix <playerID>

     */
    @Override
    public void executeCommandInGame(String[] args, Player player) {
        Player whoWillAdd;
        if (args.length == 1) {
            whoWillAdd = player;
        } else {
            whoWillAdd = Bukkit.getPlayer(args[1]);
        }
        if (whoWillAdd == null) {
            LanguageManager.languageManager.sendStringText(player, "error.player-not-found", "player", args[1]);
            return;
        }
        ObjectCache playerCache = CacheManager.cacheManager.getPlayerCache(whoWillAdd);
        if (playerCache.getActivePrefixes().isEmpty()) {
            LanguageManager.languageManager.sendStringText(player, "error.prefix-is-empty",
                    "player", whoWillAdd.getName());
            return;
        }
        LanguageManager.languageManager.sendStringText(player, "view-prefix.head",
                "player", whoWillAdd.getName());
        for (ObjectPrefix prefix: playerCache.getActivePrefixes()) {
            LanguageManager.languageManager.sendStringText(player, "view-prefix.format",
                    "player", whoWillAdd.getName(),
                    "prefix", prefix.getDisplayValue(whoWillAdd));
        }
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        Player whoWillAdd;
        if (args.length == 1) {
            LanguageManager.languageManager.sendStringText("error.args");
            return;
        } else {
            whoWillAdd = Bukkit.getPlayer(args[1]);
        }
        if (whoWillAdd == null) {
            LanguageManager.languageManager.sendStringText("error.player-not-found", "player", args[1]);
            return;
        }
        ObjectCache playerCache = CacheManager.cacheManager.getPlayerCache(whoWillAdd);
        if (playerCache.getActivePrefixes().isEmpty()) {
            LanguageManager.languageManager.sendStringText("error.prefix-is-empty",
                    "player", whoWillAdd.getName());
            return;
        }
        for (ObjectPrefix prefix: playerCache.getActivePrefixes()) {
            LanguageManager.languageManager.sendStringText("view-prefix.format",
                    "player", whoWillAdd.getName(),
                    "prefix", prefix.getDisplayValue(whoWillAdd));
        }
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
        }
        return tempVal1;
    }
}
