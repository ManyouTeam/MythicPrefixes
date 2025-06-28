package cn.superiormc.mythicprefixes.manager;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.listeners.CacheListener;
import org.bukkit.Bukkit;

public class ListenerManager {

    public static ListenerManager listenerManager;

    public ListenerManager(){
        listenerManager = this;
        registerListeners();
    }

    private void registerListeners(){
        Bukkit.getPluginManager().registerEvents(new CacheListener(), MythicPrefixes.instance);
    }
}
