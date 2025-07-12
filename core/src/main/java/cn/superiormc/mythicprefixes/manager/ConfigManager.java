package cn.superiormc.mythicprefixes.manager;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.libreforge.LibreforgeEffects;
import cn.superiormc.mythicprefixes.objects.ObjectDisplayPlaceholder;
import cn.superiormc.mythicprefixes.objects.buttons.ObjectButton;
import cn.superiormc.mythicprefixes.objects.buttons.ObjectPrefix;
import cn.superiormc.mythicprefixes.utils.TextUtil;
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

    public Collection<ObjectPrefix> prefixCaches = new TreeSet<>();

    public Map<Integer, ObjectButton> buttonConfigs = new TreeMap<>();

    public Map<String, ObjectDisplayPlaceholder> placeholderConfigs = new HashMap<>();

    public ConfigManager() {
        configManager = this;
        MythicPrefixes.instance.saveDefaultConfig();
        this.config = MythicPrefixes.instance.getConfig();
        initLibreforgeHook();
        initPrefixesConfigs();
        initDisplayPlaceholderConfigs();
        initButtonConfigs();
    }

    private void initLibreforgeHook() {
        if (ConfigManager.configManager.getBoolean("libreforge-hook")) {
            Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §fHooking into libreforge...");
            try {
                if (LibreforgeEffects.libreforgeEffects == null) {
                    new LibreforgeEffects();
                } else {
                    LibreforgeEffects.libreforgeEffects.cleanMap();
                }
            } catch (Exception ignored) {
                Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §cFailed to hook into.");
            }
        }
    }

    private void initDisplayPlaceholderConfigs() {
        File dir = new File(MythicPrefixes.instance.getDataFolder(), "display_placeholders");
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
                placeholderConfigs.put(substring, new ObjectDisplayPlaceholder(substring, YamlConfiguration.loadConfiguration(file)));
                Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §fLoaded display placeholder: " +
                        fileName + "!");
            }
        }

        // Legacy Support
        if (placeholderConfigs.isEmpty()) {
            ConfigurationSection tempVal1 = config.getConfigurationSection("display-placeholder");
            if (tempVal1 == null) {
                return;
            }
            for (String id : tempVal1.getKeys(false)) {
                placeholderConfigs.put(id, new ObjectDisplayPlaceholder(id, tempVal1.getConfigurationSection(id)));
                Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §fLoaded display placeholder: " +
                        id + "!");
            }
        }
    }

    private void initButtonConfigs() {
        ConfigurationSection tempVal1 = config.getConfigurationSection("choose-prefix-gui.custom-item");
        if (tempVal1 == null) {
            return;
        }
        for (String id : tempVal1.getKeys(false)) {
            buttonConfigs.put(Integer.parseInt(id), new ObjectButton(tempVal1.getConfigurationSection(id)));
            Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §fLoaded custom button: " +
                    id + "!");
        }
    }

    private void initPrefixesConfigs() {
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
                ObjectPrefix prefix = new ObjectPrefix(substring, YamlConfiguration.loadConfiguration(file));
                prefix.initEffects();
                prefixConfigs.put(substring, prefix);
                prefixCaches.add(prefix);
                Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §fLoaded prefix: " +
                        fileName + "!");
            }
        }
    }

    public ObjectPrefix getPrefix(String id) {
        ObjectPrefix prefix = prefixConfigs.get(id);
        if (prefix == null) {
            return prefixConfigs.get(id.replace('-', '_'));
        }
        return prefix;
    }

    public ObjectDisplayPlaceholder getDisplayPlaceholder(String id) {
        ObjectDisplayPlaceholder placeholder = placeholderConfigs.get(id);
        if (placeholder == null) {
            return placeholderConfigs.get(id.replace('-', '_'));
        }
        return placeholder;
    }

    public Collection<ObjectPrefix> getPrefixesWithoutHide() {
        Collection<ObjectPrefix> resultPrefixes = new TreeSet<>();
        for (ObjectPrefix prefix : prefixCaches) {
            if (prefix.getDisplayInGUI()) {
                resultPrefixes.add(prefix);
            }
        }
        return resultPrefixes;
    }

    public Collection<ObjectPrefix> getPrefixes() {
        return prefixCaches;
    }

    public Map<Integer, ObjectButton> getButtons() {
        return buttonConfigs;
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

    public long getLong(String path, long defaultValue) {
        return config.getLong(path, defaultValue);
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

    public ConfigurationSection getConfigurationSection(String path) {
        if (!config.contains(path)) {
            return null;
        }
        return config.getConfigurationSection(path);
    }

    public FileConfiguration getSection() {
        return config;
    }

    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

}
