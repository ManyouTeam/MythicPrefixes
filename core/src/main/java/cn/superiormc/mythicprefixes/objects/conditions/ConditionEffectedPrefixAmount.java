package cn.superiormc.mythicprefixes.objects.conditions;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.api.MythicPrefixesAPI;
import cn.superiormc.mythicprefixes.utils.TextUtil;
import org.bukkit.entity.Player;

public class ConditionEffectedPrefixAmount extends AbstractCheckCondition {

    public ConditionEffectedPrefixAmount() {
        super("effected_prefix_amount");
        setRequiredArgs("amount");
    }

    @Override
    protected boolean onCheckCondition(ObjectSingleCondition singleCondition, Player player) {
        if (MythicPrefixes.freeVersion) {
            return true;
        }
        TextUtil.sendMessage(null, MythicPrefixesAPI.getActivedPrefixesHasEffect(player).size() + "");
        return MythicPrefixesAPI.getActivedPrefixesHasEffect(player).size() >= singleCondition.getInt("amount", 0);
    }
}
