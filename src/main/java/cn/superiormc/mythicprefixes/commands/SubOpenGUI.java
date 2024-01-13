package cn.superiormc.mythicprefixes.commands;

import cn.superiormc.mythicprefixes.gui.ChoosePrefixGUI;
import cn.superiormc.mythicprefixes.objects.ObjectCommand;
import org.bukkit.entity.Player;

public class SubOpenGUI extends ObjectCommand {


    public SubOpenGUI() {
        this.id = "opengui";
        this.requiredPermission =  "mythicprefixes." + id;
        this.onlyInGame = true;
        this.requiredArgLength = new Integer[]{1};
    }

    /* Usage:

    /prefix opengui

     */
    @Override
    public void executeCommandInGame(String[] args, Player player) {
        ChoosePrefixGUI gui = new ChoosePrefixGUI(player);
        gui.openGUI();
    }
}
