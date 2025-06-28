package cn.superiormc.mythicprefixes.objects.conditions;

import cn.superiormc.mythicprefixes.manager.ConditionManager;
import cn.superiormc.mythicprefixes.objects.AbstractSingleRun;
import cn.superiormc.mythicprefixes.objects.ObjectCondition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ObjectSingleCondition extends AbstractSingleRun {

    private final ObjectCondition condition;

    public ObjectSingleCondition(ObjectCondition condition, ConfigurationSection conditionSection) {
        super(conditionSection);
        this.condition = condition;
    }

    public boolean checkBoolean(Player player) {
        return ConditionManager.conditionManager.checkBoolean(this, player);
    }

    public ObjectCondition getCondition() {
        return condition;
    }

}
