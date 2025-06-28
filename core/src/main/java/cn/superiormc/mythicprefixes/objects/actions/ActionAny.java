package cn.superiormc.mythicprefixes.objects.actions;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.objects.ObjectAction;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ActionAny extends AbstractRunAction {

    public ActionAny() {
        super("any");
        setRequiredArgs("actions");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, Player player) {
        if (MythicPrefixes.freeVersion) {
            return;
        }
        ConfigurationSection chanceSection = singleAction.getSection().getConfigurationSection("actions");
        if (chanceSection == null) {
            return;
        }
        ObjectAction action = new ObjectAction(chanceSection);
        action.runRandomEveryActions(player, singleAction.getInt("amount", 1));
    }
}
