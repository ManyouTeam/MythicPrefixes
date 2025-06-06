package cn.superiormc.mythicprefixes.commands;

import cn.superiormc.mythicprefixes.manager.CommandManager;
import cn.superiormc.mythicprefixes.objects.AbstractCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class MainCommandTab implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tempVal1 = new ArrayList<>();
        if (args.length == 1) {
            for (AbstractCommand object : CommandManager.commandManager.getSubCommandsMap().values()) {
                if (object.getRequiredPermission() != null && !object.getRequiredPermission().isEmpty()
                        && !sender.hasPermission(object.getRequiredPermission())) {
                    continue;
                }
                tempVal1.add(object.getId());
            }
        } else {
            AbstractCommand tempVal2 = CommandManager.commandManager.getSubCommandsMap().get(args[0]);
            if (tempVal2 != null && tempVal2.getRequiredPermission() != null && sender.hasPermission(tempVal2.getRequiredPermission())) {
                AbstractCommand object = CommandManager.commandManager.getSubCommandsMap().get(args[0]);
                tempVal1 = object.getTabResult(args.length);
            }
        }
        return tempVal1;
    }
}
