package cn.superiormc.mythicprefixes.database;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.manager.ErrorManager;
import cn.superiormc.mythicprefixes.objects.DynamicPrefixRequest;
import cn.superiormc.mythicprefixes.objects.ObjectCache;
import cn.superiormc.mythicprefixes.objects.buttons.ObjectPrefix;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class YamlDatabase extends AbstractDatabase {

    private final Object pendingRequestLock = new Object();

    private final File dataDir = new File(MythicPrefixes.instance.getDataFolder(), "datas");

    private final File pendingFile = new File(dataDir, "pending-dynamic-prefixes.yml");

    @Override
    public void checkData(ObjectCache cache) {
        CompletableFuture.runAsync(() -> loadData(cache), DatabaseExecutor.getExecutor());
    }

    private void loadData(ObjectCache cache) {
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        File file = new File(dataDir, cache.getPlayer().getUniqueId() + ".yml");
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
                ErrorManager.errorManager.sendErrorMessage("\u00a7cError: Can not create new data file: " + cache.getPlayer().getUniqueId() + ".yml!");
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String key : config.getConfigurationSection("dynamic-prefix-values") == null ? new ArrayList<String>() : config.getConfigurationSection("dynamic-prefix-values").getKeys(false)) {
            cache.setDynamicPrefixValue(key, config.getString("dynamic-prefix-values." + key));
        }
        loadPendingData(cache);
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

    @Override
    public void updateData(ObjectCache cache, boolean quitServer) {
        CompletableFuture.runAsync(() -> {
            saveData(cache);
            if (quitServer) {
                CacheManager.cacheManager.removePlayerCache(cache);
            }
        }, DatabaseExecutor.getExecutor());
    }

    private void saveData(ObjectCache cache) {
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        File file = new File(dataDir, cache.getPlayer().getUniqueId() + ".yml");
        if (file.exists()) {
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
        data.put("playerName", cache.getPlayer().getName());
        data.put("dynamic-prefix-values", cache.getDynamicPrefixValues());
        for (String key : data.keySet()) {
            config.set(key, data.get(key));
        }
        try {
            if (file.exists()) {
                file.delete();
            }
            config.save(file);
        } catch (IOException e) {
            ErrorManager.errorManager.sendErrorMessage("\u00a7cError: Can not save data file: " + file.getName() + "!");
        }
    }

    @Override
    public void updateDataOnDisable(ObjectCache cache, boolean disable) {
        saveData(cache);
        CacheManager.cacheManager.removePlayerCache(cache);
    }

    @Override
    public CompletableFuture<Void> saveDynamicPrefixRequest(Player player, String prefixID, String value) {
        return CompletableFuture.runAsync(() -> {
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            synchronized (pendingRequestLock) {
                YamlConfiguration config = loadPendingFile();
                String path = "requests." + player.getUniqueId() + "." + prefixID;
                config.set(path + ".playerName", player.getName());
                config.set(path + ".value", value);
                savePendingFile(config);
            }
            File file = new File(dataDir, player.getUniqueId() + ".yml");
            YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(file);
            ObjectCache cache = CacheManager.cacheManager.getPlayerCache(player);
            String markedValue = cache == null ? ObjectCache.markDynamicPrefixPending(value) : cache.getDynamicPrefixValue(prefixID);
            playerConfig.set("playerName", player.getName());
            playerConfig.set("dynamic-prefix-values." + prefixID, markedValue == null ? ObjectCache.markDynamicPrefixPending(value) : markedValue);
            savePlayerFile(playerConfig, file);
        }, DatabaseExecutor.getExecutor());
    }

    @Override
    public CompletableFuture<Collection<DynamicPrefixRequest>> getPendingDynamicPrefixRequests() {
        return CompletableFuture.supplyAsync(() -> {
            List<DynamicPrefixRequest> result = new ArrayList<>();
            YamlConfiguration config;
            synchronized (pendingRequestLock) {
                config = loadPendingFile();
            }
            if (config.getConfigurationSection("requests") == null) {
                return result;
            }
            for (String uuid : config.getConfigurationSection("requests").getKeys(false)) {
                if (config.getConfigurationSection("requests." + uuid) == null) {
                    continue;
                }
                for (String prefixID : config.getConfigurationSection("requests." + uuid).getKeys(false)) {
                    String path = "requests." + uuid + "." + prefixID;
                    String value = config.getString(path + ".value");
                    if (value == null || value.isEmpty()) {
                        continue;
                    }
                    result.add(new DynamicPrefixRequest(uuid, config.getString(path + ".playerName", uuid), prefixID, value));
                }
            }
            return result;
        }, DatabaseExecutor.getExecutor());
    }

    @Override
    public CompletableFuture<Integer> getPendingDynamicPrefixRequestAmount() {
        return getPendingDynamicPrefixRequests().thenApply(Collection::size);
    }

    @Override
    public CompletableFuture<Boolean> approveDynamicPrefixRequest(String playerUUID, String prefixID) {
        return handleDynamicPrefixRequest(playerUUID, prefixID, true);
    }

    @Override
    public CompletableFuture<Boolean> denyDynamicPrefixRequest(String playerUUID, String prefixID) {
        return handleDynamicPrefixRequest(playerUUID, prefixID, false);
    }

    private CompletableFuture<Boolean> handleDynamicPrefixRequest(String playerUUID, String prefixID, boolean approve) {
        return CompletableFuture.supplyAsync(() -> {
            synchronized (pendingRequestLock) {
                YamlConfiguration pendingConfig = loadPendingFile();
                String requestPath = "requests." + playerUUID + "." + prefixID;
                String value = pendingConfig.getString(requestPath + ".value");
                if (value == null) {
                    return false;
                }
                if (!dataDir.exists()) {
                    dataDir.mkdirs();
                }
                File file = new File(dataDir, playerUUID + ".yml");
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                if (approve) {
                    config.set("dynamic-prefix-values." + prefixID, ObjectCache.markDynamicPrefixApproved(value));
                } else {
                    String approvedValue = ObjectCache.parseApprovedDynamicPrefixValue(config.getString("dynamic-prefix-values." + prefixID));
                    config.set("dynamic-prefix-values." + prefixID, ObjectCache.markDynamicPrefixDenied(approvedValue, value));
                }
                pendingConfig.set(requestPath, null);
                try {
                    config.save(file);
                    savePendingFile(pendingConfig);
                } catch (IOException e) {
                    ErrorManager.errorManager.sendErrorMessage("\u00a7cError: Can not save data file: " + file.getName() + "!");
                    return false;
                }
                return true;
            }
            
        }, DatabaseExecutor.getExecutor());
    }

    @Override
    public CompletableFuture<Void> saveDynamicPrefixValue(String playerUUID, String prefixID, String value) {
        return CompletableFuture.runAsync(() -> {
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            File file = new File(dataDir, playerUUID + ".yml");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.set("dynamic-prefix-values." + prefixID, value);
            savePlayerFile(config, file);
        }, DatabaseExecutor.getExecutor());
    }

    @Override
    public CompletableFuture<Void> clearDynamicPrefixValue(String playerUUID, String prefixID) {
        return CompletableFuture.runAsync(() -> {
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            File file = new File(dataDir, playerUUID + ".yml");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.set("dynamic-prefix-values." + prefixID, null);
            savePlayerFile(config, file);
        }, DatabaseExecutor.getExecutor());
    }

    private YamlConfiguration loadPendingFile() {
        return YamlConfiguration.loadConfiguration(pendingFile);
    }

    private void savePendingFile(YamlConfiguration config) {
        try {
            config.save(pendingFile);
        } catch (IOException e) {
            ErrorManager.errorManager.sendErrorMessage("\u00a7cError: Can not save dynamic prefix requests: " + pendingFile.getName() + "!");
        }
    }

    private void savePlayerFile(YamlConfiguration config, File file) {
        try {
            config.save(file);
        } catch (IOException e) {
            ErrorManager.errorManager.sendErrorMessage("\u00a7cError: Can not save data file: " + file.getName() + "!");
        }
    }

    private void loadPendingData(ObjectCache cache) {
        YamlConfiguration config = loadPendingFile();
        String playerUUID = cache.getPlayer().getUniqueId().toString();
        if (config.getConfigurationSection("requests." + playerUUID) == null) {
            return;
        }
        for (String prefixID : config.getConfigurationSection("requests." + playerUUID).getKeys(false)) {
            cache.setPendingDynamicPrefixValue(prefixID, config.getString("requests." + playerUUID + "." + prefixID + ".value"));
        }
    }
}
