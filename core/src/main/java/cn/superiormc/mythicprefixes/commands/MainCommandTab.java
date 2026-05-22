package cn.superiormc.mythicprefixes.commands;

import cn.superiormc.mythicprefixes.manager.CommandManager;
import cn.superiormc.mythicprefixes.objects.AbstractCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainCommandTab implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tempVal1 = new ArrayList<>();
        if (args.length == 1) {
            for (AbstractCommand object : CommandManager.commandManager.getSubCommandsMap().values()) {
                if (!canUse(sender, object)) {
                    continue;
                }
                tempVal1.add(object.getId());
            }
        } else {
            AbstractCommand object = CommandManager.commandManager.getSubCommandsMap().get(args[0].toLowerCase(Locale.ROOT));
            if (object != null && canUse(sender, object)) {
                tempVal1 = object.getTabResult(args);
            }
        }
        return tempVal1;
    }

    private boolean canUse(CommandSender sender, AbstractCommand command) {
        String permission = command.getRequiredPermission();
        return permission == null || permission.isEmpty()
                || sender.hasPermission(permission)
                || sender.hasPermission("mythicprefixes.admin");
    }
}
