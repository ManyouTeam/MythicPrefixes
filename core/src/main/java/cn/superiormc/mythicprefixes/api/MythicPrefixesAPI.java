package cn.superiormc.mythicprefixes.api;

import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.manager.ErrorManager;
import cn.superiormc.mythicprefixes.objects.ObjectCache;
import cn.superiormc.mythicprefixes.objects.ObjectCondition;
import cn.superiormc.mythicprefixes.objects.buttons.ObjectPrefix;
import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.objects.effect.*;
import cn.superiormc.mythicprefixes.utils.CommonUtil;
import cn.superiormc.mythicprefixes.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class MythicPrefixesAPI {

    public static EffectStatus startEffect(ObjectPrefix prefix, Player player) {
        EffectStatus effectStatus = new EffectStatus();
        ConfigurationSection section = prefix.getConfig().getConfigurationSection("effects");
        if (section != null) {
            for (String tempVal1 : section.getKeys(false)) {
                if (tempVal1.equals("enabled")) {
                    continue;
                }
                ConfigurationSection tempVal3 = section.getConfigurationSection(tempVal1);
                if (tempVal3 == null) {
                    continue;
                }
                AbstractEffect tempVal2 = null;
                switch (tempVal3.getString("type", "MythicLib")) {
                    case "MythicLib":
                        if (CommonUtil.checkPluginLoad("MythicLib")) {
                            tempVal2 = new ObjectMMOEffect(prefix.getId() + tempVal1,
                                    player,
                                    tempVal3);
                        }
                        break;
                    case "MythicMobs":
                        if (CommonUtil.checkPluginLoad("MythicMobs")) {
                            tempVal2 = new ObjectMMEffect(prefix.getId() + tempVal1,
                                    player,
                                    tempVal3);
                        }
                        break;
                    case "AuraSkills":
                        if (CommonUtil.checkPluginLoad("AuraSkills")) {
                            tempVal2 = new ObjectAuraSkillsEffect(prefix.getId() + tempVal1,
                                    player,
                                    tempVal3);
                        }
                        break;
                }
                if (tempVal2 != null) {
                    if (tempVal2.getCondition().getAllBoolean(player)) {
                        tempVal2.addPlayerStat();
                        effectStatus.addAcvtiedEffects(tempVal2);
                    } else {
                        effectStatus.addNotActivedEffects(tempVal2);
                    }
                }
            }
        }
        return effectStatus;
    }
    
    public static Collection<ObjectPrefix> getActivedPrefixes(Player player) {
        if (CacheManager.cacheManager == null) {
            ErrorManager.errorManager.sendErrorMessage("§cError: Can not get cache object in plugin, " +
                    "please try restart the server.");

            return new TreeSet<>();
        } else if (CacheManager.cacheManager.getPlayerCache(player) == null) {
            return new TreeSet<>();
        } else {
            return CacheManager.cacheManager.getPlayerCache(player).getActivePrefixes();
        }
    }

    public static Collection<ObjectPrefix> getActivedPrefixesHasEffect(Player player) {
        Collection<ObjectPrefix> prefixes = new HashSet<>();
        for (ObjectPrefix prefix : getActivedPrefixes(player)) {
            if (!prefix.getEffectStatus(player).getActivedEffects().isEmpty()) {
                prefixes.add(prefix);
            }
        }
        return prefixes;
    }

    public static int getMaxPrefixesAmount(Player player, String groupID) {
        ConfigurationSection section;
        if (groupID == null) {
            section = ConfigManager.configManager.getConfigurationSection("max-prefixes-amount.default");
            if (section == null) {
                section = ConfigManager.configManager.getConfigurationSection("max-prefixes-amount");
                if (section == null) {
                    return 1;
                }
            }
        } else {
            section = ConfigManager.configManager.getConfigurationSection("max-prefixes-amount." + groupID);
            if (section == null) {
                return Integer.MAX_VALUE;
            }
        }
        ConfigurationSection conditionSection = ConfigManager.configManager.getConfigurationSection("max-prefixes-amount-conditions");
        if (conditionSection == null) {
            return section.getInt("default", 1);
        }
        Set<String> groupNameSet = conditionSection.getKeys(false);
        List<Integer> result = new ArrayList<>();
        for (String groupName : groupNameSet) {
            ObjectCondition condition = new ObjectCondition(conditionSection.getConfigurationSection(groupName));
            if (section.getInt(groupName, 0) > 0 && condition.getAllBoolean(player)) {
                result.add(section.getInt(groupName));
            }
            else {
                if (section.getInt("default") > 0) {
                    result.add(section.getInt("default", 1));
                }
            }
        }
        if (result.isEmpty()) {
            result.add(1);
        }
        return Collections.max(result);
    }

    public static String getStatusPlaceholder(ObjectPrefix prefix, ObjectCache cache) {
        switch (prefix.getPrefixStatus(cache)) {
            case USING:
                return TextUtil.parse(ConfigManager.configManager.getString(cache.getPlayer(), "status-placeholder.using"));
            case CAN_USE:
                return TextUtil.parse(ConfigManager.configManager.getString(cache.getPlayer(), "status-placeholder.unlocked"));
            case MAX_LIMIT_REACHED:
                return TextUtil.parse(ConfigManager.configManager.getString(cache.getPlayer(), "status-placeholder.max-reached"));
            case CONDITION_NOT_MEET:
                return TextUtil.parse(ConfigManager.configManager.getString(cache.getPlayer(), "status-placeholder.locked"));
        }
        return "";
    }
}
