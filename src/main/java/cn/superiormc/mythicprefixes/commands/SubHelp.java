package cn.superiormc.mythicprefixes.commands;

import cn.superiormc.mythicprefixes.manager.LanguageManager;
import cn.superiormc.mythicprefixes.objects.ObjectCommand;
import org.bukkit.entity.Player;

public class SubHelp extends ObjectCommand {

    public SubHelp() {
        this.id = "help";
        this.onlyInGame = false;
        this.requiredArgLength = new Integer[]{1};
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        if (player.hasPermission("ultimateshop.admin")) {
            LanguageManager.languageManager.sendStringText(player, "help.main-admin");
            return;
        }
        LanguageManager.languageManager.sendStringText(player, "help.main");
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        LanguageManager.languageManager.sendStringText("help.main-console");
    }
}
