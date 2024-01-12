package cn.superiormc.mythicprefixes.objects;

import cn.superiormc.mythicprefixes.api.MythicPrefixesAPI;
import cn.superiormc.mythicprefixes.libreforge.LibreforgeEffects;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.utils.CommonUtil;
import cn.superiormc.mythicprefixes.utils.ItemUtil;
import cn.superiormc.mythicprefixes.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ObjectPrefix implements Comparable {

    private String id;

    private YamlConfiguration config;

    private ObjectCondition condition;

    private Map<Player, Collection<ObjectMMOEffect>> mmoEffects = new HashMap<>();


    public ObjectPrefix(String id, YamlConfiguration config) {
        this.id = id;
        this.config = config;
        this.condition = new ObjectCondition(config.getStringList("conditions"));
        initEffects();
    }

    private void initEffects() {
        if (ConfigManager.configManager.getBoolean("libreforge-hook") &&
                config.getBoolean("effects.libreforge", false)) {
            LibreforgeEffects.libreforgeEffects.registerLibreforgeEffect(id);
        }
    }

    public boolean useMMOEffect() {
        return CommonUtil.checkPluginLoad("MythicLib") &&
                config.getBoolean("effects.MythicLib", false);
    }

    public void startMMOEffect(Player player) {
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

    public void endMMOEffect(Player player) {
        if (mmoEffects.get(player) != null) {
            for (ObjectMMOEffect tempVal1 : mmoEffects.get(player)) {
                tempVal1.removePlayerStat();
                mmoEffects.remove(tempVal1);
            }
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

    public PrefixStatus getConditionMeet(Player player) {
        if (!condition.getBoolean(player)) {
            return PrefixStatus.CONDITION_NOT_MEET;
        }
        if (MythicPrefixesAPI.getMaxPrefixesAmount(player) == MythicPrefixesAPI.getActivedPrefixes(player).size()) {
            return PrefixStatus.MAX_LIMIT_REACHED;
        }
        if (MythicPrefixesAPI.getActivedPrefixes(player).contains(this)) {
            return PrefixStatus.USING;
        }
        return PrefixStatus.CAN_USE;
    }

    public boolean getDisplayInGUI() {
        return config.contains("display-item", true);
    }

    public ItemStack getDisplayItem(Player player) {
        ConfigurationSection section = null;
        if (getConditionMeet(player) == PrefixStatus.CAN_USE) {
            section = config.getConfigurationSection("display-item.unlocked");
        } else if (getConditionMeet(player) == PrefixStatus.CONDITION_NOT_MEET) {
            section = config.getConfigurationSection("display-item.locked");
        } else if (getConditionMeet(player) == PrefixStatus.MAX_LIMIT_REACHED) {
            section = config.getConfigurationSection("display-item.max-reached");
        } else if (getConditionMeet(player) == PrefixStatus.USING) {
            section = config.getConfigurationSection("display-item.using");
        }
        if (section == null) {
            if (config.getConfigurationSection("display-item") != null) {
                return ItemUtil.buildItemStack(player, config.getConfigurationSection("display-item"));
            }
            return new ItemStack(Material.STONE);
        }
        return ItemUtil.buildItemStack(player, section,
                "display-value", getDisplayValue(player),
                "status", MythicPrefixesAPI.getStatusPlaceholder(this, player));
    }

    @Override
    public int compareTo(@NotNull Object o) {
        ObjectPrefix otherPrefix = (ObjectPrefix) o;
        if (getWeight() == otherPrefix.getWeight()) {
            int len1 = getId().length();
            int len2 = otherPrefix.getId().length();
            int minLength = Math.min(len1, len2);

            for (int i = 0; i < minLength; i++) {
                char c1 = getId().charAt(i);
                char c2 = otherPrefix.getId().charAt(i);

                if (c1 != c2) {
                    return c1 - c2;
                }
            }

            return len1 - len2;
        }
        return getWeight() - otherPrefix.getWeight();
    }

    @Override
    public String toString() {
        return getId();
    }
}
