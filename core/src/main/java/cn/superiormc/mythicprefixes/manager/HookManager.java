package cn.superiormc.mythicprefixes.manager;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.papi.PlaceholderAPIExpansion;
import cn.superiormc.mythicprefixes.utils.CommonUtil;
import cn.superiormc.mythicprefixes.utils.TextUtil;
import org.bukkit.Bukkit;

public class HookManager {

    public static HookManager hookManager;

    public PlaceholderAPIExpansion papi = null;

    public HookManager() {
        hookManager = this;
        initNormalHook();
    }

    private void initNormalHook() {
        if (CommonUtil.checkPluginLoad("PlaceholderAPI")) {
            papi = new PlaceholderAPIExpansion(MythicPrefixes.instance);
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fHooking into PlaceholderAPI...");
            if (papi.register()){
                TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fFinished hook!");
            }
        }
        if (!MythicPrefixes.freeVersion && CommonUtil.getClass("org.geysermc.floodgate.api.FloodgateApi")) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fHooking into Floodgate...");
            MythicPrefixes.useGeyser = true;
        }
    }
}
