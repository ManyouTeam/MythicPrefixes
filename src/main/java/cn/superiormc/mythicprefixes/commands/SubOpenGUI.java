package cn.superiormc.mythicprefixes.commands;

import cn.superiormc.mythicprefixes.gui.inv.ChoosePrefixGUI;
import cn.superiormc.mythicprefixes.manager.LanguageManager;
import cn.superiormc.mythicprefixes.objects.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SubOpenGUI extends AbstractCommand {


    public SubOpenGUI() {
        this.id = "opengui";
        this.requiredPermission =  "mythicprefixes." + id;
        this.onlyInGame = false;
        this.requiredArgLength = new Integer[]{1, 2};
    }

    /* Usage:

    /prefix opengui

     */
    @Override
    public void executeCommandInGame(String[] args, Player player) {
        if (args.length > 1) {
            LanguageManager.languageManager.sendStringText(player, "error.args");
            return;
        }
        ChoosePrefixGUI.openGUI(player);
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
        ChoosePrefixGUI.openGUI(whoWillAdd);
    }
}
