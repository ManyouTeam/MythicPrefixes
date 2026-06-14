package cn.superiormc.mythicprefixes.commands;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.manager.LanguageManager;
import cn.superiormc.mythicprefixes.manager.TaskManager;
import cn.superiormc.mythicprefixes.methods.ReloadPlugin;
import cn.superiormc.mythicprefixes.objects.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

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
        executeReload(args, player);
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        executeReload(args, Bukkit.getConsoleSender());
    }

    @Override
    public List<String> getTabResult(int length) {
        if (length == 2) {
            return List.of("all");
        }
        return List.of();
    }

    private void executeReload(String[] args, CommandSender sender) {
        if (args.length == 2 && !"all".equalsIgnoreCase(args[1])) {
            LanguageManager.languageManager.sendStringText(sender, "error.args");
            return;
        }
        ReloadPlugin.reload(sender, args.length == 2);
    }
}
