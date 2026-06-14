package cn.superiormc.mythicprefixes.commands;

import cn.superiormc.mythicprefixes.methods.ReloadPlugin;
import cn.superiormc.mythicprefixes.objects.AbstractCommand;
import org.bukkit.Bukkit;
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
        ReloadPlugin.reload(player);
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        ReloadPlugin.reload(Bukkit.getConsoleSender());
    }

}
