package cn.superiormc.mythicprefixes.commands;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.manager.LanguageManager;
import cn.superiormc.mythicprefixes.manager.TaskManager;
import cn.superiormc.mythicprefixes.objects.AbstractCommand;
import org.bukkit.entity.Player;

public class SubReload extends AbstractCommand {


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
        MythicPrefixes.instance.reloadConfig();
        TaskManager.taskManager.cancelTask();
        new ConfigManager();
        new LanguageManager();
        new TaskManager();
        CacheManager.cacheManager.reload();
        LanguageManager.languageManager.sendStringText(player, "plugin.reloaded");
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        MythicPrefixes.instance.reloadConfig();
        TaskManager.taskManager.cancelTask();
        new ConfigManager();
        new LanguageManager();
        new TaskManager();
        CacheManager.cacheManager.reload();
        LanguageManager.languageManager.sendStringText("plugin.reloaded");
    }
}
