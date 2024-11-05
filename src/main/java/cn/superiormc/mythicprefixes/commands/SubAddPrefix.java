package cn.superiormc.mythicprefixes.commands;

import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.manager.LanguageManager;
import cn.superiormc.mythicprefixes.objects.ObjectCache;
import cn.superiormc.mythicprefixes.objects.AbstractCommand;
import cn.superiormc.mythicprefixes.objects.buttons.ObjectPrefix;
import cn.superiormc.mythicprefixes.objects.PrefixStatus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SubAddPrefix extends AbstractCommand {


    public SubAddPrefix() {
        this.id = "addprefix";
        this.requiredPermission =  "mythicprefixes." + id;
        this.onlyInGame = false;
        this.requiredArgLength = new Integer[]{2, 3};
    }

    /* Usage:

    /prefix addprefix <prefixID>
    /prefix addprefix <playerID> <prefixID>

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
        ObjectPrefix whatPrefix = ConfigManager.configManager.getPrefix(args[args.length - 1]);
        if (whatPrefix == null) {
            LanguageManager.languageManager.sendStringText(player, "error.prefix-not-found",
                    "prefix", args[args.length - 1]);
            return;
        }
        ObjectCache playerCache = CacheManager.cacheManager.getPlayerCache(whoWillAdd);
        if (whatPrefix.getConditionMeet(playerCache) == PrefixStatus.USING) {
            LanguageManager.languageManager.sendStringText(player, "error.prefix-is-using",
                    "player", whoWillAdd.getName(),
                    "prefix", args[args.length - 1]);
            return;
        }
        if (whatPrefix.getConditionMeet(playerCache) == PrefixStatus.MAX_LIMIT_REACHED) {
            LanguageManager.languageManager.sendStringText(player, "error.max-prefix-reached",
                    "player", whoWillAdd.getName(),
                    "prefix", args[args.length - 1]);
            return;
        }
        if (whatPrefix.getConditionMeet(playerCache) == PrefixStatus.CONDITION_NOT_MEET) {
            LanguageManager.languageManager.sendStringText(player, "error.prefix-condition-not-meet",
                    "player", whoWillAdd.getName(),
                    "prefix", args[args.length - 1]);
            return;
        }
        playerCache.addActivePrefix(whatPrefix);
        LanguageManager.languageManager.sendStringText(player, "success-add-prefix",
                "player", whoWillAdd.getName(),
                "prefix", args[args.length - 1]);
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        Player whoWillAdd = Bukkit.getPlayer(args[1]);
        if (whoWillAdd == null) {
            LanguageManager.languageManager.sendStringText(Bukkit.getConsoleSender(), "error.player-not-found", "player", args[1]);
            return;
        }
        ObjectPrefix whatPrefix = ConfigManager.configManager.getPrefix(args[2]);
        if (whatPrefix == null) {
            LanguageManager.languageManager.sendStringText(Bukkit.getConsoleSender(), "error.prefix-not-found", "prefix", args[2]);
            return;
        }
        ObjectCache playerCache = CacheManager.cacheManager.getPlayerCache(whoWillAdd);
        playerCache.addActivePrefix(whatPrefix);
        LanguageManager.languageManager.sendStringText(Bukkit.getConsoleSender(), "success-add-prefix",
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
