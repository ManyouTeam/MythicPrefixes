package cn.superiormc.mythicprefixes.manager;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.listeners.CacheListener;
import cn.superiormc.mythicprefixes.listeners.DupeListener;
import cn.superiormc.mythicprefixes.listeners.GUIListener;
import cn.superiormc.mythicprefixes.utils.CommonUtil;
import cn.superiormc.mythicprefixes.utils.PacketInventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public class ListenerManager {

    public static ListenerManager listenerManager;

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

    public void unregisterAllListener() {
        HandlerList.unregisterAll(MythicPrefixes.instance);
        if (MythicPrefixes.usePacketEvents && PacketInventoryUtil.packetInventoryUtil != null) {
            PacketInventoryUtil.packetInventoryUtil.shutdown();
        }
    }
}
