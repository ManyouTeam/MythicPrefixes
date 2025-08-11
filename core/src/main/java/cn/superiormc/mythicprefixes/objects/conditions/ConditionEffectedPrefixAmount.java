package cn.superiormc.mythicprefixes.objects.conditions;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.api.MythicPrefixesAPI;
import org.bukkit.Bukkit;
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
        Bukkit.getConsoleSender().sendMessage(MythicPrefixesAPI.getActivedPrefixesHasEffect(player).size() + "");
        return MythicPrefixesAPI.getActivedPrefixesHasEffect(player).size() >= singleCondition.getInt("amount", 0);
    }
}
