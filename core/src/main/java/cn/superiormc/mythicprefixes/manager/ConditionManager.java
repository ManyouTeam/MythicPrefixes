package cn.superiormc.mythicprefixes.manager;

import cn.superiormc.mythicprefixes.objects.conditions.AbstractCheckCondition;
import cn.superiormc.mythicprefixes.objects.conditions.ConditionAny;
import cn.superiormc.mythicprefixes.objects.conditions.ConditionBiome;
import cn.superiormc.mythicprefixes.objects.conditions.ConditionEffectedPrefixAmount;
import cn.superiormc.mythicprefixes.objects.conditions.ConditionEquippedPrefix;
import cn.superiormc.mythicprefixes.objects.conditions.ConditionEquippedPrefixAmount;
import cn.superiormc.mythicprefixes.objects.conditions.ConditionNot;
import cn.superiormc.mythicprefixes.objects.conditions.ConditionPermission;
import cn.superiormc.mythicprefixes.objects.conditions.ConditionPlaceholder;
import cn.superiormc.mythicprefixes.objects.conditions.ConditionWorld;
import cn.superiormc.mythicprefixes.objects.conditions.ObjectSingleCondition;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ConditionManager extends AbstractManager {

    public static ConditionManager conditionManager;

    private Map<String, AbstractCheckCondition> conditions;

    public ConditionManager() {
        conditionManager = this;
        initConditions();
    }

    private void initConditions() {
        conditions = new HashMap<>();
        registerNewCondition("biome", new ConditionBiome());
        registerNewCondition("permission", new ConditionPermission());
        registerNewCondition("placeholder", new ConditionPlaceholder());
        registerNewCondition("world", new ConditionWorld());
        registerNewCondition("any", new ConditionAny());
        registerNewCondition("not", new ConditionNot());
        registerNewCondition("equipped_prefix", new ConditionEquippedPrefix());
        registerNewCondition("equipped_prefix_amount", new ConditionEquippedPrefixAmount());
        registerNewCondition("effected_prefix_amount", new ConditionEffectedPrefixAmount());
    }

    public void registerNewCondition(String actionID,
                                  AbstractCheckCondition condition) {
        if (!conditions.containsKey(actionID)) {
            conditions.put(actionID, condition);
        }
    }

    public boolean checkBoolean(ObjectSingleCondition condition, Player player) {
        for (AbstractCheckCondition checkCondition : conditions.values()) {
            String type = condition.getString("type");
            if (checkCondition.getType().equals(type) && !checkCondition.checkCondition(condition, player)) {
                return false;
            }
        }
        return true;
    }
}
