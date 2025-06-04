package cn.superiormc.mythicprefixes.manager;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.papi.PlaceholderAPIExpansion;
import cn.superiormc.mythicprefixes.utils.CommonUtil;
import cn.superiormc.mythicprefixes.utils.TextUtil;
import org.bukkit.Bukkit;

public class HookManager {

    public static HookManager hookManager;

    public HookManager() {
        hookManager = this;
        initNormalHook();
    }

    private void initNormalHook() {
        if (CommonUtil.checkPluginLoad("PlaceholderAPI")) {
            PlaceholderAPIExpansion.papi = new PlaceholderAPIExpansion(MythicPrefixes.instance);
            Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §fHooking into PlaceholderAPI...");
            if (PlaceholderAPIExpansion.papi.register()){
                Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §fFinished hook!");
            }
        }
        if (!MythicPrefixes.freeVersion && CommonUtil.getClass("org.geysermc.floodgate.api.FloodgateApi")) {
            Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §fHooking into Floodgate...");
            MythicPrefixes.useGeyser = true;
        }
    }
}
