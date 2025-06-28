package cn.superiormc.mythicprefixes.utils;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CommonUtil {

    public static boolean checkPluginLoad(String pluginName){
        return MythicPrefixes.instance.getServer().getPluginManager().isPluginEnabled(pluginName);
    }

    public static boolean checkPermission(Player player, String permission) {
        if (CommonUtil.checkPluginLoad("LuckPerms")) {
            return LuckPermsProvider.get().getPlayerAdapter(Player.class).
                    getPermissionData(player).
                    checkPermission(permission).asBoolean();
        } else {
            return player.hasPermission(permission);
        }
    }

    public static boolean getClass(String className) {
        try {
            Class.forName(className);
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean checkClass(String className, String methodName) {
        try {
            Class<?> targetClass = Class.forName(className);
            Method[] methods = targetClass.getDeclaredMethods();

            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    return true;
                }
            }

            return false;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean getMajorVersion(int version) {
        return MythicPrefixes.majorVersion >= version;
    }

    public static boolean getMinorVersion(int majorVersion, int minorVersion) {
        return MythicPrefixes.majorVersion > majorVersion || (MythicPrefixes.majorVersion == majorVersion &&
                MythicPrefixes.minorVersion >= minorVersion);
    }

    public static void mkDir(File dir) {
        if (!dir.exists()) {
            File parentFile = dir.getParentFile();
            if (parentFile == null) {
                return;
            }
            String parentPath = parentFile.getPath();
            mkDir(new File(parentPath));
            dir.mkdir();
        }
    }

    public static String modifyString(String text, String... args) {
        for (int i = 0 ; i < args.length ; i += 2) {
            String var = "{" + args[i] + "}";
            if (args[i + 1] == null) {
                text = text.replace(var, "");
            }
            else {
                text = text.replace(var, args[i + 1]);
            }
        }
        return text;
    }

    public static List<String> modifyList(Player player, List<String> config, String... args) {
        List<String> resultList = new ArrayList<>();
        for (String s : config) {
            for (int i = 0 ; i < args.length ; i += 2) {
                String var = "{" + args[i] + "}";
                if (args[i + 1] == null) {
                    s = s.replace(var, "");
                }
                else {
                    s = s.replace(var, args[i + 1]);
                }
            }
            String[] tempVal1 = s.split(";;");
            if (tempVal1.length > 1) {
                for (String string : tempVal1) {
                    resultList.add(TextUtil.withPAPI(string, player));
                }
                continue;
            }
            resultList.add(TextUtil.withPAPI(s, player));
        }
        return resultList;
    }

    public static NamespacedKey parseNamespacedKey(String key) {
        String[] keySplit = key.split(":");
        if (keySplit.length == 1) {
            return NamespacedKey.minecraft(key.toLowerCase());
        }
        if (CommonUtil.getMajorVersion(16)) {
            return NamespacedKey.fromString(key);
        }
        return new NamespacedKey("mythicprefixes", "unknown");
    }

    public static boolean isBedrockPlayer(Player player) {
        if (!MythicPrefixes.useGeyser || !ConfigManager.configManager.getBoolean("choose-prefix-gui.bedrock.enabled")) {
            return false;
        }
        if (ConfigManager.configManager.getString("choose-prefix-gui.bedrock.check-method", "FLOODGATE").equalsIgnoreCase("FLOODGATE")) {
            return FloodgateApi.getInstance().getPlayer(player.getUniqueId()) != null;
        } else {
            return player.getUniqueId().toString().startsWith("00000000-0000-0000-000");
        }
    }
}
