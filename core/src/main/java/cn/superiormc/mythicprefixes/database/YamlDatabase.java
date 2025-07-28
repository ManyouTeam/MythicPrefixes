package cn.superiormc.mythicprefixes.database;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.objects.ObjectCache;
import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.manager.ErrorManager;
import cn.superiormc.mythicprefixes.objects.buttons.ObjectPrefix;
import cn.superiormc.mythicprefixes.utils.SchedulerUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YamlDatabase {

    public static void checkData(ObjectCache cache) {
        File dir = new File(MythicPrefixes.instance.getDataFolder() + "/datas");
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(dir, cache.getPlayer().getUniqueId() + ".yml");
        if (!file.exists()) {
            YamlConfiguration config = new YamlConfiguration();
            Map<String, Object> data = new HashMap<>();
            try {
                data.put("playerName", cache.getPlayer().getName());
                for (String key : data.keySet()) {
                    config.set(key, data.get(key));
                }
                config.save(file);
            } catch (IOException e) {
                ErrorManager.errorManager.sendErrorMessage("§cError: Can not create new data file: " + cache.getPlayer().getUniqueId() + ".yml!");
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        StringBuilder tempVal3 = new StringBuilder();
        int i = 0;
        List<String> tempVal1 = config.getStringList("prefixID");
        if (!tempVal1.isEmpty()) {
            for (String tempVal2 : tempVal1) {
                if (i > 0) {
                    tempVal3.append(";;");
                }
                tempVal3.append(tempVal2);
                i++;
            }
            cache.setActivePrefixes(tempVal3.toString());
        }
    }

    public static void updateData(ObjectCache cache, boolean quitServer) {
        File dir = new File(MythicPrefixes.instance.getDataFolder() + "/datas");
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(dir, cache.getPlayer().getUniqueId() + ".yml");
        if (file.exists()){
            file.delete();
        }
        YamlConfiguration config = new YamlConfiguration();
        Map<String, Object> data = new HashMap<>();
        List<String> prefixes = new ArrayList<>();
        for (ObjectPrefix single : CacheManager.cacheManager.getPlayerCache(
                cache.getPlayer()).getActivePrefixes()) {
            prefixes.add(single.getId());
        }
        data.put("prefixID", prefixes);
        for (String key : data.keySet()) {
            config.set(key, data.get(key));
        }
        try {
            if (file.exists()) {
                file.delete();
            }
            config.save(file);
            if (quitServer) {
                CacheManager.cacheManager.removePlayerCache(cache.getPlayer());
            }
        } catch (IOException e) {
            ErrorManager.errorManager.sendErrorMessage("§cError: Can not save data file: " + file.getName() + "!");
        }
    }

}
