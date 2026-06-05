package cn.superiormc.mythicprefixes.manager;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.gui.InvGUI;
import cn.superiormc.mythicprefixes.listeners.CacheListener;
import cn.superiormc.mythicprefixes.listeners.DupeListener;
import cn.superiormc.mythicprefixes.listeners.GUIListener;
import cn.superiormc.mythicprefixes.utils.CommonUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ListenerManager {

    public static ListenerManager listenerManager;

    private final Map<UUID, InvGUI> listeners = new HashMap<>();

    public ListenerManager(){
        listenerManager = this;
        registerListeners();
    }

    private void registerListeners(){
        Bukkit.getPluginManager().registerEvents(new GUIListener(), MythicPrefixes.instance);
        Bukkit.getPluginManager().registerEvents(new CacheListener(), MythicPrefixes.instance);
        if (CommonUtil.getMajorVersion(19) && MythicPrefixes.methodUtil.methodID().equals("paper") &&
                ConfigManager.configManager.getBoolean("choose-prefix-gui.anti-dupe-checker")) {
            Bukkit.getPluginManager().registerEvents(new DupeListener(), MythicPrefixes.instance);
        }
    }

    public void registerNewGUIListener(Player player, InvGUI inv) {
        unregisterListeners(player);
        listeners.put(player.getUniqueId(), inv);
    }

    public void unregisterNewGUIListener(Player player, InvGUI inv) {
        listeners.remove(player.getUniqueId(), inv);
    }

    public void unregisterListeners(Player player) {
        listeners.remove(player.getUniqueId());
    }

    public InvGUI getInvGUI(Player player) {
        return listeners.get(player.getUniqueId());
    }

    public void unregisterAllListener() {
        HandlerList.unregisterAll(MythicPrefixes.instance);
    }
}
