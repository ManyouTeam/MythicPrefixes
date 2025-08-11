package cn.superiormc.mythicprefixes.objects.conditions;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.api.MythicPrefixesAPI;
import org.bukkit.entity.Player;

public class ConditionEquippedPrefixAmount extends AbstractCheckCondition {

    public ConditionEquippedPrefixAmount() {
        super("equipped_prefix_amount");
        setRequiredArgs("amount");
    }

    @Override
    protected boolean onCheckCondition(ObjectSingleCondition singleCondition, Player player) {
        if (MythicPrefixes.freeVersion) {
            return true;
        }
        return MythicPrefixesAPI.getActivedPrefixes(player).size() >= singleCondition.getInt("amount", 0);
    }
}
