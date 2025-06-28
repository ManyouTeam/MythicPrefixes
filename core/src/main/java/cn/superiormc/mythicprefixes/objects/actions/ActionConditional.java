package cn.superiormc.mythicprefixes.objects.actions;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.objects.ObjectAction;
import cn.superiormc.mythicprefixes.objects.ObjectCondition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ActionConditional extends AbstractRunAction {

    public ActionConditional() {
        super("conditional");
        setRequiredArgs("actions", "conditions");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, Player player) {
        if (MythicPrefixes.freeVersion) {
            return;
        }
        ConfigurationSection conditionSection = singleAction.getSection().getConfigurationSection("conditions");
        if (conditionSection == null) {
            return;
        }
        ObjectCondition condition = new ObjectCondition(conditionSection);
        if (!condition.getAllBoolean(player)) {
            return;
        }
        ConfigurationSection actionSection = singleAction.getSection().getConfigurationSection("actions");
        if (actionSection == null) {
            return;
        }
        ObjectAction action = new ObjectAction(actionSection);
        action.runAllActions(player);
    }
}
