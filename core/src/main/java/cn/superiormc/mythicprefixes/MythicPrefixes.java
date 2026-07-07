package cn.superiormc.mythicprefixes;

import cn.superiormc.mythicprefixes.bstats.Metrics;
import cn.superiormc.mythicprefixes.database.DatabaseExecutor;
import cn.superiormc.mythicprefixes.manager.*;
import cn.superiormc.mythicprefixes.methods.DynamicPrefixes;
import cn.superiormc.mythicprefixes.objects.ObjectCache;
import cn.superiormc.mythicprefixes.utils.CommonUtil;
import cn.superiormc.mythicprefixes.utils.PacketInventoryUtil;
import cn.superiormc.mythicprefixes.utils.SpecialMethodUtil;
import cn.superiormc.mythicprefixes.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class MythicPrefixes extends JavaPlugin {

    public static MythicPrefixes instance;

    private Metrics metrics;

    public static final boolean freeVersion = true;

    public static SpecialMethodUtil methodUtil;

    public static boolean isFolia = false;

    public static boolean useGeyser = false;

    public static boolean usePacketEvents = false;

    public static int yearVersion;

    public static int majorVersion;

    public static int minorVersion;

    @Override
    public void onEnable() {
        instance = this;
        DatabaseExecutor.start();
        try {
            String[] versionParts = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
            yearVersion = versionParts.length > 0 && versionParts[0].matches("\\d+") ? Integer.parseInt(versionParts[0]) : 1;
            majorVersion = versionParts.length > 1 && versionParts[1].matches("\\d+") ? Integer.parseInt(versionParts[1]) : 0;
            minorVersion = versionParts.length > 2 && versionParts[2].matches("\\d+") ? Integer.parseInt(versionParts[2]) : 0;
        } catch (Throwable throwable) {
            Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §cError: Can not get your Minecraft version! Default set to 1.0.0.");
        }
        if (CommonUtil.getClass("com.destroystokyo.paper.PaperConfig") && CommonUtil.getMinorVersion(18, 2)) {
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
        new DatabaseManager();
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
        metrics = new Metrics(MythicPrefixes.instance, 28731);
        TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fYour server version is: " + yearVersion + "." + majorVersion + "." + minorVersion + "!");
        TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fPlugin is loaded. Author: PQguanfang.");
    }

    @Override
    public void onDisable() {
        TaskManager.taskManager.cancelTask();
        ListenerManager.listenerManager.unregisterAllListener();
        DynamicPrefixes.clearDynamicPrefixEditors();
        if (HookManager.hookManager.papi != null) {
            HookManager.hookManager.papi.unregister();
            HookManager.hookManager.papi = null;
        }
        TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fWaiting for all pending database task finished, this may freeze your server if your database is lost connection.");
        DatabaseExecutor.await();
        for (Player player : Bukkit.getOnlinePlayers()) {
            ObjectCache cache = CacheManager.cacheManager.getPlayerCache(player);
            if (cache != null) {
                cache.runAllPrefixEndActions();
            }
            CacheManager.cacheManager.savePlayerCacheOnDisable(player, true);
        }
        DatabaseManager.databaseManager.database.onClose();
        CacheManager.cacheManager.shutdown();
        DatabaseExecutor.shutdown();
        if (metrics != null) {
            metrics.shutdown();
            metrics = null;
        }
        TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fPlugin is disabled. Author: PQguanfang.");
    }
}
