package cn.superiormc.mythicprefixes.objects.buttons;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.api.MythicPrefixesAPI;
import cn.superiormc.mythicprefixes.libreforge.LibreforgeEffects;
import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.objects.*;
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
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ObjectPrefix extends AbstractButton implements Comparable<ObjectPrefix> {

    private final String id;

    private final ObjectAction startAction;

    private final ObjectAction endAction;

    private final ObjectAction circleAction;

    private final Map<Player, Collection<ObjectMMOEffect>> mmoEffects = new HashMap<>();

    private final Map<Player, SchedulerUtil> taskCache = new HashMap<>();

    private boolean useMMOEffect;

    private boolean isDefaultPrefix;

    public ObjectPrefix(String id, YamlConfiguration config) {
        super(config);
        this.id = id;
        this.type = ButtonType.PREFIX;
        this.condition = new ObjectCondition(config.getConfigurationSection("conditions"));
        this.startAction = new ObjectAction(config.getConfigurationSection("equip-actions"));
        this.endAction = new ObjectAction(config.getConfigurationSection("unequip-actions"));
        this.circleAction = new ObjectAction(config.getConfigurationSection("circle-actions"));
        initEffects();
    }

    private void initEffects() {
        if (ConfigManager.configManager.getBoolean("libreforge-hook") &&
                config.getBoolean("effects.libreforge", false)) {
            LibreforgeEffects.libreforgeEffects.registerLibreforgeEffect(id);
        }
        if (CommonUtil.checkPluginLoad("MythicLib") &&
                config.getBoolean("effects.MythicLib", false)) {
            useMMOEffect = true;
        }
    }

    public void runStartAction(Player player) {
        if (useMMOEffect) {
            startMMOEffect(player);
        }
        startAction.runAllActions(player);
        if (!circleAction.isEmpty()) {
            SchedulerUtil task = SchedulerUtil.runTaskTimer(() ->
                    circleAction.runAllActions(player), 0L, ConfigManager.configManager.getLong("circle-actions.period-tick", 20L));
            taskCache.put(player, task);
        }
    }

    public void runEndAction(Player player) {
        if (useMMOEffect) {
            endMMOEffect(player);
        }
        endAction.runAllActions(player);
        if (taskCache.get(player) != null) {
            taskCache.get(player).cancel();
        }
        taskCache.remove(player);
    }

    private void startMMOEffect(Player player) {
        ConfigurationSection section = config.getConfigurationSection("MythicLib-effects");
        Collection<ObjectMMOEffect> mmoResult = new HashSet<>();
        if (section != null) {
            for (String tempVal1 : section.getKeys(false)) {
                ObjectMMOEffect tempVal2 = new ObjectMMOEffect(id + tempVal1,
                        player,
                        section.getConfigurationSection(tempVal1).getString("stat", ""),
                        section.getConfigurationSection(tempVal1).getDouble("value", 0));
                tempVal2.addPlayerStat();
                mmoResult.add(tempVal2);
            }
        }
        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicPrefixes] §fStarted MMO effect for player " + player.getName());
        mmoEffects.put(player, mmoResult);
    }

    private void endMMOEffect(Player player) {
        if (mmoEffects.get(player) != null) {
            for (ObjectMMOEffect tempVal1 : mmoEffects.get(player)) {
                tempVal1.removePlayerStat();
            }
            mmoEffects.remove(player);
        }
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
        if (getConditionBoolean(cache)) {
            return PrefixStatus.CONDITION_NOT_MEET;
        }
        if (MythicPrefixesAPI.getMaxPrefixesAmount(player) == MythicPrefixesAPI.getActivedPrefixes(player, !isDefaultPrefix).size()) {
            return PrefixStatus.MAX_LIMIT_REACHED;
        }
        return PrefixStatus.CAN_USE;
    }

    public boolean getConditionBoolean(ObjectCache cache) {
        Player player = cache.getPlayer();
        return cache.isFinishLoad() && !condition.getAllBoolean(player) && !CommonUtil.checkPermission(player, "mythicprefixes.bypass." + getId());
    }

    public boolean getDisplayInGUI() {
        return config.contains("display-item", true) && !isDefaultPrefix;
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
