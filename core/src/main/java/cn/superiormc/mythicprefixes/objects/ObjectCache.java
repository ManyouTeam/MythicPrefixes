package cn.superiormc.mythicprefixes.objects;

import cn.superiormc.mythicprefixes.api.MythicPrefixesAPI;
import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.manager.LanguageManager;
import cn.superiormc.mythicprefixes.objects.buttons.ObjectPrefix;
import cn.superiormc.mythicprefixes.utils.SchedulerUtil;
import cn.superiormc.mythicprefixes.utils.TextUtil;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class ObjectCache {

    public static final String DYNAMIC_PENDING_SUFFIX = ";;PENDING";

    public static final String DYNAMIC_DENY_SUFFIX = ";;DENY";

    public static final String DYNAMIC_APPROVE_SUFFIX = ";;APPROVE";

    public static final String DYNAMIC_VALUE_SPLITTER = ";;";

    private final Player player;

    private final Collection<ObjectPrefix> prefixCaches = new TreeSet<>();

    private final Map<ObjectPrefix, SchedulerUtil> taskCache = new HashMap<>();

    private final Map<String, String> dynamicPrefixValues = new HashMap<>();

    private final Map<String, String> pendingDynamicPrefixValues = new HashMap<>();

    private boolean finishLoad;

    public ObjectCache(Player player) {
        this.player = player;
    }

    public void initPlayerCache() {
        CacheManager.cacheManager.database.checkData(this);
    }

    public void shutPlayerCache(boolean quitServer) {
        CacheManager.cacheManager.database.updateData(this, quitServer);
    }

    public void shutPlayerCacheOnDisable(boolean disable) {
        CacheManager.cacheManager.database.updateDataOnDisable(this, disable);

    }

    public Player getPlayer() {
        return player;
    }

    public void setActivePrefixes(String values) {
        for (String prefixKey : values.split(";;")) {
            ObjectPrefix tempVal2 = ConfigManager.configManager.getPrefix(prefixKey);
            if (tempVal2 == null) {
                continue;
            }
            SchedulerUtil.runSync(() -> addActivePrefix(tempVal2));
        }
    }

    public String getDynamicPrefixValue(String prefixID) {
        return dynamicPrefixValues.get(prefixID);
    }

    public String getCleanDynamicPrefixValue(String prefixID) {
        return cleanDynamicPrefixValue(getDynamicPrefixValue(prefixID));
    }

    public String getApprovedDynamicPrefixValue(String prefixID) {
        return parseApprovedDynamicPrefixValue(getDynamicPrefixValue(prefixID));
    }

    public void setDynamicPrefixValue(String prefixID, String value) {
        if (value == null || value.isEmpty()) {
            dynamicPrefixValues.remove(prefixID);
            return;
        }
        if (isDeniedDynamicPrefixValue(value)) {
            SchedulerUtil.runSync(() -> LanguageManager.languageManager.sendStringText(player,
                    "dynamic-prefix.denied-player", "prefix", prefixID));
            String approvedValue = parseApprovedDynamicPrefixValue(value);
            if (approvedValue == null || approvedValue.isEmpty()) {
                dynamicPrefixValues.remove(prefixID);
                CacheManager.cacheManager.database.clearDynamicPrefixValue(player.getUniqueId().toString(), prefixID);
            } else {
                dynamicPrefixValues.put(prefixID, approvedValue);
                CacheManager.cacheManager.database.saveDynamicPrefixValue(player.getUniqueId().toString(), prefixID, approvedValue);
            }
            return;
        }
        if (isApprovedDynamicPrefixValue(value)) {
            SchedulerUtil.runSync(() -> LanguageManager.languageManager.sendStringText(player,
                    "dynamic-prefix.approved-player", "prefix", prefixID));
            String approvedValue = parseApprovedDynamicPrefixValue(value);
            if (approvedValue == null || approvedValue.isEmpty()) {
                dynamicPrefixValues.remove(prefixID);
                CacheManager.cacheManager.database.clearDynamicPrefixValue(player.getUniqueId().toString(), prefixID);
            } else {
                dynamicPrefixValues.put(prefixID, approvedValue);
                CacheManager.cacheManager.database.saveDynamicPrefixValue(player.getUniqueId().toString(), prefixID, approvedValue);
            }
            return;
        }
        String pendingValue = getPendingReviewDynamicPrefixValue(value);
        if (pendingValue != null && !pendingValue.isEmpty()) {
            setPendingDynamicPrefixValue(prefixID, pendingValue);
        }
        dynamicPrefixValues.put(prefixID, value);
    }

    public void clearDynamicPrefixValue(String prefixID) {
        dynamicPrefixValues.remove(prefixID);
    }

    public static String markDynamicPrefixPending(String value) {
        return value + DYNAMIC_PENDING_SUFFIX;
    }

    public static String markDynamicPrefixPending(String approvedValue, String pendingValue) {
        if (approvedValue == null || approvedValue.isEmpty()) {
            return markDynamicPrefixPending(pendingValue);
        }
        return approvedValue + DYNAMIC_VALUE_SPLITTER + pendingValue + DYNAMIC_PENDING_SUFFIX;
    }

    public static String markDynamicPrefixDenied(String value) {
        return value + DYNAMIC_DENY_SUFFIX;
    }

    public static String markDynamicPrefixDenied(String approvedValue, String deniedValue) {
        if (approvedValue == null || approvedValue.isEmpty()) {
            return markDynamicPrefixDenied(deniedValue);
        }
        return approvedValue + DYNAMIC_VALUE_SPLITTER + deniedValue + DYNAMIC_DENY_SUFFIX;
    }

    public static String markDynamicPrefixApproved(String value) {
        return value + DYNAMIC_APPROVE_SUFFIX;
    }

    public static boolean isPendingDynamicPrefixValue(String value) {
        return value != null && value.endsWith(DYNAMIC_PENDING_SUFFIX);
    }

    public static boolean isDeniedDynamicPrefixValue(String value) {
        return value != null && value.endsWith(DYNAMIC_DENY_SUFFIX);
    }

    public static boolean isApprovedDynamicPrefixValue(String value) {
        return value != null && value.endsWith(DYNAMIC_APPROVE_SUFFIX);
    }

    public static boolean isBlockedDynamicPrefixValue(String value) {
        return isPendingDynamicPrefixValue(value) || isDeniedDynamicPrefixValue(value) || isApprovedDynamicPrefixValue(value);
    }

    public static String cleanDynamicPrefixValue(String value) {
        return parseApprovedDynamicPrefixValue(value);
    }

    public static String parseApprovedDynamicPrefixValue(String value) {
        if (isPendingDynamicPrefixValue(value)) {
            String body = value.substring(0, value.length() - DYNAMIC_PENDING_SUFFIX.length());
            int splitIndex = body.lastIndexOf(DYNAMIC_VALUE_SPLITTER);
            if (splitIndex < 0) {
                return null;
            }
            return body.substring(0, splitIndex);
        }
        if (isDeniedDynamicPrefixValue(value)) {
            String body = value.substring(0, value.length() - DYNAMIC_DENY_SUFFIX.length());
            int splitIndex = body.lastIndexOf(DYNAMIC_VALUE_SPLITTER);
            if (splitIndex < 0) {
                return null;
            }
            return body.substring(0, splitIndex);
        }
        if (isApprovedDynamicPrefixValue(value)) {
            String body = value.substring(0, value.length() - DYNAMIC_APPROVE_SUFFIX.length());
            int splitIndex = body.lastIndexOf(DYNAMIC_VALUE_SPLITTER);
            if (splitIndex < 0) {
                return body;
            }
            return body.substring(splitIndex + DYNAMIC_VALUE_SPLITTER.length());
        }
        return value;
    }

    public static String getPendingReviewDynamicPrefixValue(String value) {
        if (!isPendingDynamicPrefixValue(value)) {
            return null;
        }
        String body = value.substring(0, value.length() - DYNAMIC_PENDING_SUFFIX.length());
        int splitIndex = body.lastIndexOf(DYNAMIC_VALUE_SPLITTER);
        if (splitIndex < 0) {
            return body;
        }
        return body.substring(splitIndex + DYNAMIC_VALUE_SPLITTER.length());
    }

    public Map<String, String> getDynamicPrefixValues() {
        return new HashMap<>(dynamicPrefixValues);
    }

    public String getPendingDynamicPrefixValue(String prefixID) {
        String value = pendingDynamicPrefixValues.get(prefixID);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        return getPendingReviewDynamicPrefixValue(getDynamicPrefixValue(prefixID));
    }

    public void setPendingDynamicPrefixValue(String prefixID, String value) {
        if (value == null || value.isEmpty()) {
            pendingDynamicPrefixValues.remove(prefixID);
            return;
        }
        pendingDynamicPrefixValues.put(prefixID, value);
    }

    public void clearPendingDynamicPrefixValue(String prefixID) {
        pendingDynamicPrefixValues.remove(prefixID);
    }

    public Map<String, String> getPendingDynamicPrefixValues() {
        return new HashMap<>(pendingDynamicPrefixValues);
    }

    public void addActivePrefix(ObjectPrefix prefix) {
        if (prefix.getPrefixStatus(this) != PrefixStatus.CAN_USE) {
            return;
        }
        prefix.runStartAction(this);
        prefixCaches.add(prefix);
        if (ConfigManager.configManager.getBoolean("debug")) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fEnabled prefix " + prefix + " for player " + player.getName() + "!");
        }
    }

    public void removeActivePrefix(ObjectPrefix prefix, boolean runEndAction) {
        if (!prefixCaches.contains(prefix)) {
            return;
        }
        if (runEndAction) {
            prefix.runEndAction(this);
        }
        prefixCaches.remove(prefix);
        if (ConfigManager.configManager.getBoolean("debug")) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fDisabled prefix " + prefix + " for player " + player.getName() + "!");
        }
    }

    public void removeAllActivePrefix(boolean runEndAction) {
        for (ObjectPrefix prefix : MythicPrefixesAPI.getActivedPrefixes(player)) {
            removeActivePrefix(prefix, runEndAction);
        }
    }

    public void runAllPrefixEndActions() {
        for (ObjectPrefix prefix : MythicPrefixesAPI.getActivedPrefixes(player)) {
            prefix.runEndAction(this);
        }
    }

    public Collection<ObjectPrefix> getActivePrefixes() {
        return new TreeSet<>(prefixCaches);
    }

    public String getActivePrefixesID() {
        int i = 0;
        StringBuilder tempVal2 = new StringBuilder();
        for (ObjectPrefix tempVal1 : getActivePrefixes()) {
            if (i > 0) {
                tempVal2.append(";;");
            }
            tempVal2.append(tempVal1.getId());
            i++;
        }
        return tempVal2.toString();
    }

    public void checkCondition() {
        if (!isFinishLoad()) {
            return;
        }
        for (ObjectPrefix prefix : MythicPrefixesAPI.getActivedPrefixes(player)) {
            if (prefix.isConditionNotMeet(this) || prefix.isConditionNotMeet(this)) {
                removeActivePrefix(prefix, true);
            }
        }
    }

    public void setAsFinished() {
        finishLoad = true;
    }

    public boolean isFinishLoad() {
        if (!ConfigManager.configManager.getBoolean("cache.bypass-condition-when-loading")) {
            return true;
        }
        return finishLoad;
    }

    public void addCircleTask(ObjectPrefix prefix, SchedulerUtil schedulerUtil) {
        taskCache.put(prefix, schedulerUtil);
    }

    public void cancelCircleTask(ObjectPrefix prefix) {
        SchedulerUtil schedulerUtil = taskCache.get(prefix);
        if (schedulerUtil != null) {
            schedulerUtil.cancel();
        }
        taskCache.remove(prefix);
    }
}
