package cn.superiormc.mythicprefixes.commands;

import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.manager.LanguageManager;
import cn.superiormc.mythicprefixes.objects.ObjectCommand;
import org.bukkit.entity.Player;

public class SubReload extends ObjectCommand {


    public SubReload() {
        this.id = "reload";
        this.requiredPermission =  "mythicprefixes." + id;
        this.onlyInGame = false;
        this.requiredArgLength = new Integer[]{1};
    }

    /* Usage:

    /prefix reload

     */
    @Override
    public void executeCommandInGame(String[] args, Player player) {
        new ConfigManager();
        new LanguageManager();
        CacheManager.cacheManager.reload();
        LanguageManager.languageManager.sendStringText(player, "plugin.reloaded");
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        new ConfigManager();
        new LanguageManager();
        CacheManager.cacheManager.reload();
        LanguageManager.languageManager.sendStringText("plugin.reloaded");
    }
}
