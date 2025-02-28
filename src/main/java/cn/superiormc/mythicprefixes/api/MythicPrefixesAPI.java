package cn.superiormc.mythicprefixes.api;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.manager.ErrorManager;
import cn.superiormc.mythicprefixes.objects.ObjectCache;
import cn.superiormc.mythicprefixes.objects.ObjectCondition;
import cn.superiormc.mythicprefixes.objects.buttons.ObjectPrefix;
import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.objects.effect.AbstractEffect;
import cn.superiormc.mythicprefixes.objects.effect.ObjectAuraSkillsEffect;
import cn.superiormc.mythicprefixes.objects.effect.ObjectMMEffect;
import cn.superiormc.mythicprefixes.objects.effect.ObjectMMOEffect;
import cn.superiormc.mythicprefixes.utils.CommonUtil;
import cn.superiormc.mythicprefixes.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class MythicPrefixesAPI {

    public static Collection<AbstractEffect> startEffect(ObjectPrefix prefix, Player player) {
        ConfigurationSection section = prefix.getConfig().getConfigurationSection("effects");
        Collection<AbstractEffect> mmoResult = new HashSet<>();
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
                        if (CommonUtil.checkPluginLoad("MythicMobs") && !MythicPrefixes.freeVersion) {
                            tempVal2 = new ObjectMMEffect(prefix.getId() + tempVal1,
                                    player,
                                    tempVal3);
                        }
                        break;
                    case "AuraSkills":
                        if (CommonUtil.checkPluginLoad("AuraSkills") && !MythicPrefixes.freeVersion) {
                            tempVal2 = new ObjectAuraSkillsEffect(prefix.getId() + tempVal1,
                                    player,
                                    tempVal3);
                        }
                        break;
                }
                if (tempVal2 != null) {
                    tempVal2.addPlayerStat();
                    mmoResult.add(tempVal2);
                }
            }
        }
        return mmoResult;
    }
    
    public static Collection<ObjectPrefix> getActivedPrefixes(Player player) {
        if (CacheManager.cacheManager == null) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicPrefixes] §cError: Can not get cache object in plugin, " +
                    "please try restart the server.");

            return new TreeSet<>();
        } else if (CacheManager.cacheManager.getPlayerCache(player) == null) {
            return new TreeSet<>();
        } else {
            return CacheManager.cacheManager.getPlayerCache(player).getActivePrefixes();
        }
    }

    public static int getMaxPrefixesAmount(Player player) {
        ConfigurationSection section = MythicPrefixes.instance.getConfig().
                getConfigurationSection("max-prefixes-amount");
        if (section == null) {
            return 1;
        }
        ConfigurationSection conditionSection = MythicPrefixes.instance.getConfig().
                getConfigurationSection("max-prefixes-amount-conditions");
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
                return TextUtil.parse(ConfigManager.configManager.getString("status-placeholder.using"));
            case CAN_USE:
                return TextUtil.parse(ConfigManager.configManager.getString("status-placeholder.unlocked"));
            case MAX_LIMIT_REACHED:
                return TextUtil.parse(ConfigManager.configManager.getString("status-placeholder.max-reached"));
            case CONDITION_NOT_MEET:
                return TextUtil.parse(ConfigManager.configManager.getString("status-placeholder.locked"));
        }
        return "";
    }
}
