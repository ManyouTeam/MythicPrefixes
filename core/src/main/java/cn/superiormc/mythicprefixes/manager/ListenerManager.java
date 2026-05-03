package cn.superiormc.mythicprefixes.manager;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.listeners.CacheListener;
import cn.superiormc.mythicprefixes.listeners.DupeListener;
import cn.superiormc.mythicprefixes.utils.CommonUtil;
import org.bukkit.Bukkit;

public class ListenerManager {

    public static ListenerManager listenerManager;

    public ListenerManager(){
        listenerManager = this;
        registerListeners();
    }

    private void registerListeners(){
        Bukkit.getPluginManager().registerEvents(new CacheListener(), MythicPrefixes.instance);
        if (CommonUtil.getMajorVersion(19) && MythicPrefixes.methodUtil.methodID().equals("paper") &&
                ConfigManager.configManager.getBoolean("choose-prefix-gui.anti-dupe-checker")) {
            Bukkit.getPluginManager().registerEvents(new DupeListener(), MythicPrefixes.instance);
        }
    }
}
