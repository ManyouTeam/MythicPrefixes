package cn.superiormc.mythicprefixes.objects.conditions;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.objects.ObjectCondition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ConditionNot extends AbstractCheckCondition {

    public ConditionNot() {
        super("not");
        setRequiredArgs("conditions");
    }

    @Override
    protected boolean onCheckCondition(ObjectSingleCondition singleCondition, Player player) {
        if (MythicPrefixes.freeVersion) {
            return true;
        }
        ConfigurationSection anySection = singleCondition.getSection().getConfigurationSection("conditions");
        if (anySection == null) {
            return true;
        }
        ObjectCondition condition = new ObjectCondition(anySection);
        return !condition.getAllBoolean(player);
    }
}
