package cn.superiormc.mythicprefixes.commands;

import cn.superiormc.mythicprefixes.gui.ChoosePrefixGUI;
import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.manager.LanguageManager;
import cn.superiormc.mythicprefixes.objects.ObjectCache;
import cn.superiormc.mythicprefixes.objects.ObjectCommand;
import cn.superiormc.mythicprefixes.objects.ObjectPrefix;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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
