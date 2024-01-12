package cn.superiormc.mythicprefixes.manager;

import cn.superiormc.mythicprefixes.commands.*;
import cn.superiormc.mythicprefixes.objects.ObjectCommand;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommandManager {

    public static CommandManager commandManager;

    private Map<String, ObjectCommand> registeredCommands = new HashMap<>();

    public CommandManager(){
        commandManager = this;
        registerBukkitCommand();
        registerObjectCommand();
    }

    private void registerBukkitCommand(){
        Objects.requireNonNull(Bukkit.getPluginCommand("mythicprefixes")).setExecutor(new MainCommand());
        Objects.requireNonNull(Bukkit.getPluginCommand("mythicprefixes")).setTabCompleter(new MainCommandTab());
    }

    private void registerObjectCommand() {
        registeredCommands.put("addprefix", new SubAddPrefix());
        registeredCommands.put("removeprefix", new SubRemovePrefix());
        registeredCommands.put("setprefix", new SubSetPrefix());
        registeredCommands.put("viewusingprefix", new SubViewUsingPrefix());
        registeredCommands.put("reload", new SubReload());
        registeredCommands.put("opengui", new SubOpenGUI());
    }

    public Map<String, ObjectCommand> getSubCommandsMap() {
        return registeredCommands;
    }
}
