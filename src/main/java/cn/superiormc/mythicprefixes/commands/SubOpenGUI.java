package cn.superiormc.mythicprefixes.commands;

import cn.superiormc.mythicprefixes.gui.ChoosePrefixGUI;
import cn.superiormc.mythicprefixes.manager.LanguageManager;
import cn.superiormc.mythicprefixes.objects.ObjectCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SubOpenGUI extends ObjectCommand {


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
        ChoosePrefixGUI gui = new ChoosePrefixGUI(player);
        gui.openGUI();
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        Player whoWillAdd = Bukkit.getPlayer(args[1]);
        if (whoWillAdd == null) {
            LanguageManager.languageManager.sendStringText("error.player-not-found", "player", args[1]);
            return;
        }
        ChoosePrefixGUI gui = new ChoosePrefixGUI(whoWillAdd);
        gui.openGUI();
    }
}
