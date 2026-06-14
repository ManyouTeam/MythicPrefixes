package cn.superiormc.mythicprefixes.objects.buttons;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.api.MythicPrefixesAPI;
import cn.superiormc.mythicprefixes.libreforge.LibreforgeEffects;
import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.manager.LanguageManager;
import cn.superiormc.mythicprefixes.methods.DynamicPrefixes;
import cn.superiormc.mythicprefixes.objects.*;
import cn.superiormc.mythicprefixes.objects.effect.AbstractEffect;
import cn.superiormc.mythicprefixes.objects.effect.EffectStatus;
import cn.superiormc.mythicprefixes.utils.CommonUtil;
import cn.superiormc.mythicprefixes.utils.ItemUtil;
import cn.superiormc.mythicprefixes.utils.TextUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ObjectPrefix extends AbstractButton implements Comparable<ObjectPrefix> {

    private final String id;

    private final ObjectAction startAction;

    private final ObjectAction endAction;

    private final ObjectAction circleAction;

    private final long circleActionPeriodTick;

    private final ObjectAction clickActionCDM;

    private final ObjectAction clickActionMXR;

    private final Map<Player, EffectStatus> mmoEffects = new HashMap<>();

    private boolean useEffect;

    private boolean isDefaultPrefix;

    private final List<String> groups;

    private final boolean dynamicPrefix;

    public ObjectPrefix(String id, YamlConfiguration config) {
        super(config);
        this.id = id;
        this.type = ButtonType.PREFIX;
        this.condition = new ObjectCondition(config.getConfigurationSection("conditions"));
        this.startAction = new ObjectAction(config.getConfigurationSection("equip-actions"));
        this.endAction = new ObjectAction(config.getConfigurationSection("unequip-actions"));
        this.circleAction = new ObjectAction(config.getConfigurationSection("circle-actions"));
        this.circleActionPeriodTick = Math.max(1L, config.getLong("circle-actions.period-tick",
                ConfigManager.configManager.getLong("circle-actions.period-tick", 20L)));
        this.clickActionCDM = new ObjectAction(config.getConfigurationSection("click-actions.condition-not-meet"));
        this.clickActionMXR = new ObjectAction(config.getConfigurationSection("click-actions.max-limit-reached"));
        if (!MythicPrefixes.freeVersion) {
            this.groups = config.getStringList("groups");
        } else {
            this.groups = new ArrayList<>();
        }
        this.dynamicPrefix = config.getBoolean("dynamic-prefix", false);
        ObjectDisplayPlaceholder.groupNames.addAll(groups);
    }

    public void initEffects() {
        if (config.getBoolean("effects.enabled", false)) {
            if (ConfigManager.configManager.getBoolean("libreforge-hook") &&
                    config.getBoolean("effects.enabled", false)) {
                LibreforgeEffects.libreforgeEffects.registerLibreforgeEffect(id);
            }
            useEffect = true;
        }
    }

    public boolean enabledEffect() {
        return config.getBoolean("effects.enabled", false);
    }

    public void runStartAction(ObjectCache cache) {
        Player player = cache.getPlayer();
        if (useEffect) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fStarted effect for player " + player.getName());
            mmoEffects.put(player, MythicPrefixesAPI.startEffect(this, player));
        }
        startAction.runAllActions(player);
    }

    public void runCircleAction(Player player) {
        if (!circleAction.isEmpty()) {
            circleAction.runAllActions(player);
        }
        if (mmoEffects.get(player) != null) {
            mmoEffects.get(player).retryActiveEffects();
        }
    }

    public long getCircleActionPeriodTick() {
        return circleActionPeriodTick;
    }

    public boolean requiresCircleTask() {
        return !circleAction.isEmpty() || useEffect;
    }

    public EffectStatus getEffectStatus(Player player) {
        return mmoEffects.get(player);
    }

    public void runEndAction(ObjectCache cache) {
        Player player = cache.getPlayer();
        if (useEffect) {
            if (mmoEffects.get(player) != null) {
                for (AbstractEffect tempVal1 : mmoEffects.get(player).getActivedEffects()) {
                    tempVal1.removePlayerStat();
                }
                mmoEffects.remove(player);
            }
        }
        endAction.runAllActions(player);
    }

    public String getId() {
        return id;
    }

    // Without color code
    public String getDisplayValue(Player player) {
        if (dynamicPrefix) {
            ObjectCache cache = CacheManager.cacheManager.getPlayerCache(player);
            if (cache != null) {
                String value = cache.getApprovedDynamicPrefixValue(id);
                if (value != null && !value.isEmpty()) {
                    return TextUtil.withPAPI(value, player);
                }
                cache.removeActivePrefix(this, true);
            }
            return LanguageManager.languageManager.getStringText(player, "dynamic-prefix.none");
        }
        return TextUtil.withPAPI(config.getString("display-value", "UNKNOWN"), player);
    }

    public String getPendingDisplayValue(Player player) {
        if (!dynamicPrefix) {
            return "";
        }
        ObjectCache cache = CacheManager.cacheManager.getPlayerCache(player);
        if (cache != null) {
            String value = cache.getPendingDynamicPrefixValue(id);
            if (value != null && !value.isEmpty()) {
                return TextUtil.withPAPI(value, player);
            }
        }
        return LanguageManager.languageManager.getStringText(player, "dynamic-prefix.none");
    }

    public int getWeight() {
        return config.getInt("weight");
    }

    public PrefixStatus getPrefixStatus(ObjectCache cache) {
        if (cache == null) {
            return PrefixStatus.CONDITION_NOT_MEET;
        }
        if (cache.getActivePrefixes().contains(this)) {
            return PrefixStatus.USING;
        }
        Player player = cache.getPlayer();
        if (isConditionNotMeet(cache) || isDynamicPrefixEmpty(cache)) {
            return PrefixStatus.CONDITION_NOT_MEET;
        }
        Collection<ObjectPrefix> nowPrefixes = MythicPrefixesAPI.getActivedPrefixes(player);
        if (nowPrefixes.size() >= MythicPrefixesAPI.getMaxPrefixesAmount(player, null)) {
            return PrefixStatus.MAX_LIMIT_REACHED;
        }
        if (!MythicPrefixes.freeVersion && !groups.isEmpty()) {
            for (String groupID : groups) {
                int groupNowAmount = 0;
                for (ObjectPrefix prefix : nowPrefixes) {
                    if (prefix.getGroups().contains(groupID)) {
                        groupNowAmount++;
                    }
                }
                if (groupNowAmount >= MythicPrefixesAPI.getMaxPrefixesAmount(player, groupID)) {
                    return PrefixStatus.MAX_LIMIT_REACHED;
                }
            }
        }
        return PrefixStatus.CAN_USE;
    }

    public boolean isConditionNotMeet(ObjectCache cache) {
        Player player = cache.getPlayer();
        if (!cache.conditionCanCheck()) {
            return false;
        }
        return !condition.getAllBoolean(player) && !CommonUtil.checkPermission(player, "mythicprefixes.bypass." + getId());
    }

    public boolean isDynamicPrefixEmpty(ObjectCache cache) {
        String dynamicValue = cache.getApprovedDynamicPrefixValue(id);
        return dynamicPrefix && (dynamicValue == null || dynamicValue.isEmpty());
    }

    public boolean getDisplayInGUI() {
        return config.contains("display-item", true) && !isDefaultPrefix();
    }

    public boolean isDynamicPrefix() {
        return dynamicPrefix;
    }

    public boolean shouldHideInGUI(Player player) {
        return !config.getBoolean("auto-hide", false) || condition.getAllBoolean(player);
    }

    @Override
    public void clickEvent(ClickType type, Player player) {
        if (dynamicPrefix && type.isRightClick()) {
            DynamicPrefixes.openDynamicPrefixEditor(player, this);
            return;
        }
        ObjectCache cache = CacheManager.cacheManager.getPlayerCache(player);
        if (cache == null) {
            return;
        }
        switch (getPrefixStatus(cache)) {
            case CAN_USE:
                cache.addActivePrefix(this);
                break;
            case USING:
                cache.removeActivePrefix(this, true);
                break;
            case CONDITION_NOT_MEET:
                if (!MythicPrefixes.freeVersion) {
                    clickActionCDM.runAllActions(player);
                }
                break;
            case MAX_LIMIT_REACHED:
                if (!MythicPrefixes.freeVersion) {
                    clickActionMXR.runAllActions(player);
                }
                break;
        }
    }

    @Override
    public ItemStack getDisplayItem(Player player) {
        ConfigurationSection section = null;
        ObjectCache cache = CacheManager.cacheManager.getPlayerCache(player);
        PrefixStatus status = getPrefixStatus(cache);
        if (status == PrefixStatus.CAN_USE) {
            section = config.getConfigurationSection("display-item.unlocked");
        } else if (status == PrefixStatus.CONDITION_NOT_MEET) {
            section = config.getConfigurationSection("display-item.locked");
        } else if (status == PrefixStatus.MAX_LIMIT_REACHED) {
            section = config.getConfigurationSection("display-item.max-reached");
        } else if (status == PrefixStatus.USING) {
            section = config.getConfigurationSection("display-item.using");
        }
        if (section == null) {
            if (config.getConfigurationSection("display-item") != null) {
                return ItemUtil.buildItemStack(player, config.getConfigurationSection("display-item"),
                        "display-value", getDisplayValue(player),
                        "pending-value", getPendingDisplayValue(player),
                        "status", MythicPrefixesAPI.getStatusPlaceholder(this, cache));
            }
            return new ItemStack(Material.STONE);
        }
        return ItemUtil.buildItemStack(player, section,
                "display-value", getDisplayValue(player),
                "pending-value", getPendingDisplayValue(player),
                "status", MythicPrefixesAPI.getStatusPlaceholder(this, cache));
    }

    public boolean isDefaultPrefix() {
        return !MythicPrefixes.freeVersion && isDefaultPrefix;
    }

    public void setDefaultPrefix(boolean b) {
        isDefaultPrefix = b;
    }

    public List<String> getGroups() {
        return groups;
    }

    @Override
    public int compareTo(@NotNull ObjectPrefix otherPrefix) {
        if (getWeight() == otherPrefix.getWeight()) {
            int len1 = getId().length();
            int len2 = otherPrefix.getId().length();
            int minLength = Math.min(len1, len2);

            for (int i = 0; i < minLength; i++) {
                char c1 = getId().charAt(i);
                char c2 = otherPrefix.getId().charAt(i);

                if (c1 != c2) {
                    if (Character.isDigit(c1) && Character.isDigit(c2)) {
                        // 如果字符都是数字，则按照数字大小进行比较
                        return Integer.compare(Integer.parseInt(getId().substring(i)), Integer.parseInt(otherPrefix.getId().substring(i)));
                    } else {
                        // 否则，按照字符的unicode值进行比较
                        return c1 - c2;
                    }
                }
            }

            return len1 - len2;
        }
        return getWeight() - otherPrefix.getWeight();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ObjectPrefix prefix) {
            return prefix.getId().equals(this.getId());
        }
        return false;
    }

    @Override
    public String toString() {
        return getId();
    }
}
