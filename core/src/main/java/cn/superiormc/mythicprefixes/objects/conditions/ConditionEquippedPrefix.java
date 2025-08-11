package cn.superiormc.mythicprefixes.objects.conditions;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.api.MythicPrefixesAPI;
import cn.superiormc.mythicprefixes.objects.buttons.ObjectPrefix;
import org.bukkit.entity.Player;

public class ConditionEquippedPrefix extends AbstractCheckCondition {

    public ConditionEquippedPrefix() {
        super("equipped_prefix");
        setRequiredArgs("prefixes");
    }

    @Override
    protected boolean onCheckCondition(ObjectSingleCondition singleCondition, Player player) {
        if (MythicPrefixes.freeVersion) {
            return true;
        }
        if (!singleCondition.getBoolean("require-all", true)) {
            for (String prefixID : singleCondition.getStringList("prefixes")) {
                for (ObjectPrefix prefix : MythicPrefixesAPI.getActivedPrefixes(player)) {
                    if (prefix.getId().equals(prefixID)) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            for (String prefixID : singleCondition.getStringList("prefixes")) {
                for (ObjectPrefix prefix : MythicPrefixesAPI.getActivedPrefixes(player)) {
                    if (!prefix.getId().equals(prefixID)) {
                        return false;
                    }
                }
            }
            return true;
        }
    }
}
