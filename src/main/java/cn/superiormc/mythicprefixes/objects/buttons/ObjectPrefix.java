package cn.superiormc.mythicprefixes.objects.buttons;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.api.MythicPrefixesAPI;
import cn.superiormc.mythicprefixes.libreforge.LibreforgeEffects;
import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.objects.*;
import cn.superiormc.mythicprefixes.objects.effect.AbstractEffect;
import cn.superiormc.mythicprefixes.utils.CommonUtil;
import cn.superiormc.mythicprefixes.utils.ItemUtil;
import cn.superiormc.mythicprefixes.utils.SchedulerUtil;
import cn.superiormc.mythicprefixes.utils.TextUtil;
import org.bukkit.Bukkit;
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

    private final ObjectAction clickActionCDM;

    private final ObjectAction clickActionMXR;

    private final Map<Player, Collection<AbstractEffect>> mmoEffects = new HashMap<>();

    private boolean useEffect;

    private boolean isDefaultPrefix;

    private final List<String> groups;

    public ObjectPrefix(String id, YamlConfiguration config) {
        super(config);
        this.id = id;
        this.type = ButtonType.PREFIX;
        this.condition = new ObjectCondition(config.getConfigurationSection("conditions"));
        this.startAction = new ObjectAction(config.getConfigurationSection("equip-actions"));
        this.endAction = new ObjectAction(config.getConfigurationSection("unequip-actions"));
        this.circleAction = new ObjectAction(config.getConfigurationSection("circle-actions"));
        this.clickActionCDM = new ObjectAction(config.getConfigurationSection("click-actions.condition-not-meet"));
        this.clickActionMXR = new ObjectAction(config.getConfigurationSection("click-actions.max-limit-reached"));
        if (!MythicPrefixes.freeVersion) {
            this.groups = config.getStringList("groups");
        } else {
            this.groups = new ArrayList<>();
        }
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

    public void runStartAction(ObjectCache cache) {
        Player player = cache.getPlayer();
        if (useEffect) {
            Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §fStarted effect for player " + player.getName());
            mmoEffects.put(player, MythicPrefixesAPI.startEffect(this, player));
        }
        startAction.runAllActions(player);
        if (!circleAction.isEmpty()) {
            SchedulerUtil task = SchedulerUtil.runTaskTimer(() ->
                    circleAction.runAllActions(player), 0L, ConfigManager.configManager.getLong("circle-actions.period-tick", 20L));
            cache.addCircleTask(this, task);
        }
    }

    public void runEndAction(ObjectCache cache) {
        Player player = cache.getPlayer();
        if (useEffect) {
            if (mmoEffects.get(player) != null) {
                for (AbstractEffect tempVal1 : mmoEffects.get(player)) {
                    tempVal1.removePlayerStat();
                }
                mmoEffects.remove(player);
            }
        }
        endAction.runAllActions(player);
        cache.cancelCircleTask(this);
    }

    public String getId() {
        return id;
    }

    public String getDisplayValue(Player player) {
        return TextUtil.parse(config.getString("display-value", "UNKNOWN"), player);
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
        if (isConditionNotMeet(cache)) {
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
        if (!cache.isFinishLoad()) {
            return false;
        }
        return !condition.getAllBoolean(player) && !CommonUtil.checkPermission(player, "mythicprefixes.bypass." + getId());
    }

    public boolean getDisplayInGUI() {
        return config.contains("display-item", true) && !isDefaultPrefix();
    }

    public boolean shouldHideInGUI(Player player) {
        return !config.getBoolean("auto-hide", false) || condition.getAllBoolean(player);
    }

    @Override
    public void clickEvent(ClickType type, Player player) {
        ObjectCache cache = CacheManager.cacheManager.getPlayerCache(player);
        switch (getPrefixStatus(cache)) {
            case CAN_USE:
                CacheManager.cacheManager.getPlayerCache(player).addActivePrefix(this);
                break;
            case USING:
                CacheManager.cacheManager.getPlayerCache(player).removeActivePrefix(this);
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
                        "status", MythicPrefixesAPI.getStatusPlaceholder(this, cache));
            }
            return new ItemStack(Material.STONE);
        }
        return ItemUtil.buildItemStack(player, section,
                "display-value", getDisplayValue(player),
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
        if (obj instanceof ObjectPrefix) {
            ObjectPrefix prefix = (ObjectPrefix) obj;
            return prefix.getId().equals(this.getId());
        }
        return false;
    }

    @Override
    public String toString() {
        return getId();
    }
}
