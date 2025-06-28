package cn.superiormc.mythicprefixes.objects.conditions;


import org.bukkit.entity.Player;

public class ConditionPermission extends AbstractCheckCondition {

    public ConditionPermission() {
        super("permission");
        setRequiredArgs("permission");
    }

    @Override
    protected boolean onCheckCondition(ObjectSingleCondition singleCondition, Player player) {
        return player.hasPermission(singleCondition.getString("permission"));
    }
}
