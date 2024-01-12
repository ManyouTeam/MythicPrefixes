package cn.superiormc.mythicprefixes.manager;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.libreforge.LibreforgeEffects;
import cn.superiormc.mythicprefixes.objects.ObjectDisplayPlaceholder;
import cn.superiormc.mythicprefixes.objects.ObjectPrefix;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class ConfigManager {

    public static ConfigManager configManager;

    public FileConfiguration config;

    public Map<String, ObjectPrefix> prefixConfigs = new TreeMap<>();

    public Map<String, ObjectDisplayPlaceholder> placeholderConfigs = new HashMap<>();

    public ConfigManager() {
        configManager = this;
        MythicPrefixes.instance.saveDefaultConfig();
        this.config = MythicPrefixes.instance.getConfig();
        initLibreforgeHook();
        initDisplayPlaceholderConfigs();
        initPrefixesConfigs();
    }

    private void initLibreforgeHook() {
        if (ConfigManager.configManager.getBoolean("libreforge-hook")) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicPrefixes] §fHooking into libreforge...");
            try {
                new LibreforgeEffects();
            } catch (Exception ignored) {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicPrefixes] §cFailed to hook into.");
            }
        }
    }

    private void initDisplayPlaceholderConfigs() {
        this.placeholderConfigs = new HashMap<>();
        ConfigurationSection tempVal1 = config.getConfigurationSection("display-placeholder");
        if (tempVal1 == null) {
            return;
        }
        for (String id : tempVal1.getKeys(false)) {
            placeholderConfigs.put(id, new ObjectDisplayPlaceholder(id, tempVal1.getConfigurationSection(id)));
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicPrefixes] §fLoaded display placeholder: " +
                    id + "!");
        }
    }

    private void initPrefixesConfigs() {
        this.prefixConfigs = new HashMap<>();
        File dir = new File(MythicPrefixes.instance.getDataFolder(), "prefixes");
        if (!dir.exists()) {
            dir.mkdir();
        }
        File[] files = dir.listFiles();
        if (!Objects.nonNull(files) && files.length != 0) {
            return;
        }
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.endsWith(".yml")) {
                String substring = fileName.substring(0, fileName.length() - 4);
                prefixConfigs.put(substring,
                        new ObjectPrefix(substring, YamlConfiguration.loadConfiguration(file)));
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicPrefixes] §fLoaded prefix: " +
                        fileName + "!");
            }
        }
    }

    public ObjectPrefix getPrefix(String id) {
        return prefixConfigs.get(id);
    }

    public ObjectDisplayPlaceholder getDisplayPlaceholder(String id) {
        return placeholderConfigs.get(id);
    }

    public Collection<ObjectPrefix> getPrefixesWithoutHide() {
        Collection<ObjectPrefix> resultPrefixes = new TreeSet<>();
        for (String key : prefixConfigs.keySet()) {
            ObjectPrefix prefix = prefixConfigs.get(key);
            if (prefix.getDisplayInGUI()) {
                resultPrefixes.add(prefix);
            }
        }
        return resultPrefixes;
    }

    public Collection<ObjectPrefix> getPrefixes() {
        Collection<ObjectPrefix> resultPrefixes = new TreeSet<>();
        for (String key : prefixConfigs.keySet()) {
            resultPrefixes.add(prefixConfigs.get(key));
        }
        return resultPrefixes;
    }

    public List<String> getListWithColor(String... args) {
        List<String> resultList = new ArrayList<>();
        for (String s : config.getStringList(args[0])) {
            for (int i = 1 ; i < args.length ; i += 2) {
                String var = "{" + args[i] + "}";
                if (args[i + 1] == null) {
                    s = s.replace(var, "");
                }
                else {
                    s = s.replace(var, args[i + 1]);
                }
            }
            //resultList.add(TextUtil.parse(s));
        }
        return resultList;
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path, false);
    }

    public int getInt(String path, int defaultValue) {
        return config.getInt(path, defaultValue);
    }

    public List<Integer> getIntList(String path) {
        return config.getIntegerList(path);
    }

    public String getString(String path, String... args) {
        String s = config.getString(path);
        if (s == null) {
            s = args[0];
        }
        for (int i = 1 ; i < args.length ; i += 2) {
            String var = "{" + args[i] + "}";
            if (args[i + 1] == null) {
                s = s.replace(var, "");
            }
            else {
                s = s.replace(var, args[i + 1]);
            }
        }
        return s.replace("{plugin_folder}", String.valueOf(MythicPrefixes.instance.getDataFolder()));
    }

    public FileConfiguration getSection() {
        return config;
    }

}
