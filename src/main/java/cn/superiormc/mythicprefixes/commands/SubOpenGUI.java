package cn.superiormc.mythicprefixes.commands;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.gui.inv.ChoosePrefixGUI;
import cn.superiormc.mythicprefixes.manager.LanguageManager;
import cn.superiormc.mythicprefixes.objects.AbstractCommand;
import cn.superiormc.mythicprefixes.objects.ObjectDisplayPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SubOpenGUI extends AbstractCommand {


    public SubOpenGUI() {
        this.id = "opengui";
        this.requiredPermission =  "mythicprefixes." + id;
        this.onlyInGame = false;
        this.requiredArgLength = new Integer[]{1, 2, 3};
    }

    /* Usage:

    /prefix opengui

     */
    @Override
    public void executeCommandInGame(String[] args, Player player) {
        if (args.length > 2) {
            LanguageManager.languageManager.sendStringText(player, "error.args");
            return;
        }
        String group = "all";
        if (args.length == 2 && !MythicPrefixes.freeVersion) {
            group = args[1];
        }
        ChoosePrefixGUI.openGUI(player, group);
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        if (args.length < 1) {
            LanguageManager.languageManager.sendStringText("error.args");
            return;
        }
        Player whoWillAdd = Bukkit.getPlayer(args[1]);
        if (whoWillAdd == null) {
            LanguageManager.languageManager.sendStringText("error.player-not-found", "player", args[1]);
            return;
        }
        String group = "all";
        if (args.length == 3 && !MythicPrefixes.freeVersion) {
            group = args[2];
        }
        ChoosePrefixGUI.openGUI(whoWillAdd, group);
    }

    @Override
    public List<String> getTabResult(int length) {
        List<String> tempVal1 = new ArrayList<>();
        switch (length) {
            case 2:
                tempVal1.addAll(ObjectDisplayPlaceholder.groupNames);
                break;
        }
        return tempVal1;
    }
}
