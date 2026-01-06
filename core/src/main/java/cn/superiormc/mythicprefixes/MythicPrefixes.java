package cn.superiormc.mythicprefixes;

import cn.superiormc.mythicprefixes.bstats.Metrics;
import cn.superiormc.mythicprefixes.manager.*;
import cn.superiormc.mythicprefixes.utils.CommonUtil;
import cn.superiormc.mythicprefixes.utils.PacketInventoryUtil;
import cn.superiormc.mythicprefixes.utils.SpecialMethodUtil;
import cn.superiormc.mythicprefixes.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class MythicPrefixes extends JavaPlugin {

    public static MythicPrefixes instance;

    public static final boolean freeVersion = true;

    public static SpecialMethodUtil methodUtil;

    public static boolean isFolia = false;

    public static boolean useGeyser = false;

    public static boolean usePacketEvents = false;

    public static int majorVersion;

    public static int minorVersion;

    public static boolean newSkullMethod;

    @Override
    public void onEnable() {
        instance = this;
        try {
            String[] versionParts = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
            majorVersion = versionParts.length > 1 ? Integer.parseInt(versionParts[1]) : 0;
            minorVersion = versionParts.length > 2 ? Integer.parseInt(versionParts[2]) : 0;
        } catch (Throwable throwable) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cError: Can not get your Minecraft version! Default set to 1.0.0.");
        }
        if (CommonUtil.getClass("com.destroystokyo.paper.PaperConfig") && CommonUtil.getMinorVersion(17, 1)) {
            try {
                Class<?> paperClass = Class.forName("cn.superiormc.mythicprefixes.paper.PaperMethodUtil");
                methodUtil = (SpecialMethodUtil) paperClass.getDeclaredConstructor().newInstance();
                TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fPaper is found, entering Paper plugin mode...!");
            } catch (Throwable throwable) {
                Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §cError: The plugin seems break, please download it again from site.");
                Bukkit.getPluginManager().disablePlugin(this);
            }
        } else {
            try {
                Class<?> spigotClass = Class.forName("cn.superiormc.mythicprefixes.spigot.SpigotMethodUtil");
                methodUtil = (SpecialMethodUtil) spigotClass.getDeclaredConstructor().newInstance();
                TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fSpigot is found, entering Spigot plugin mode...!");
            } catch (Throwable throwable) {
                Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §cError: The plugin seems break, please download it again from site.");
                Bukkit.getPluginManager().disablePlugin(this);
            }
        }
        if (CommonUtil.getClass("io.papermc.paper.threadedregions.RegionizedServer")) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fFolia is found, enabled Folia compatibility feature!");
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §6Warning: Folia support is not fully test, major bugs maybe found! " +
                    "Please do not use in production environment!");
            isFolia = true;
        }
        new ErrorManager();
        new InitManager();
        new ActionManager();
        new ConditionManager();
        new ConfigManager();
        new HookManager();
        new LanguageManager();
        new CacheManager();
        new CommandManager();
        new ListenerManager();
        new TaskManager();
        if (ConfigManager.configManager.getBoolean("choose-prefix-gui.title-update.enabled") && MythicPrefixes.methodUtil.methodID().equals("paper") &&
                CommonUtil.checkPluginLoad("packetevents") && !MythicPrefixes.freeVersion) {
            usePacketEvents = true;
            new PacketInventoryUtil();
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fDynamic title enabled. Hooking into packetevents...");
        }
        if (!CommonUtil.checkClass("com.mojang.authlib.properties.Property", "getValue") && CommonUtil.getMinorVersion(21, 1)) {
            newSkullMethod = true;
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fNew AuthLib found, enabled new skull get method!");
        }
        new Metrics(MythicPrefixes.instance, 28371);
        TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fYour Minecraft version is: 1." + majorVersion + "." + minorVersion + "!");
        TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fPlugin is loaded. Author: PQguanfang.");
    }

    @Override
    public void onDisable() {
        CacheManager.cacheManager.setStoppingServer();
        for (Player player : Bukkit.getOnlinePlayers()) {
            CacheManager.cacheManager.getPlayerCache(player).runAllPrefixEndActions();
            CacheManager.cacheManager.savePlayerCacheOnDisable(player, true);
        }
        CacheManager.cacheManager.database.onClose();
        TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fPlugin is disabled. Author: PQguanfang.");
    }
}
