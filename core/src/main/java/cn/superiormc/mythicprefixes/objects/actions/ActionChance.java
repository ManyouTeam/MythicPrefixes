package cn.superiormc.mythicprefixes.objects.actions;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.objects.ObjectAction;
import org.apache.commons.lang3.RandomUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ActionChance extends AbstractRunAction {

    public ActionChance() {
        super("chance");
        setRequiredArgs("rate", "actions");
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
        double rate = singleAction.getDouble("rate");
        if (RandomUtils.nextDouble(0, 100) > rate) {
            ObjectAction action = new ObjectAction(chanceSection);
            action.runAllActions(player);
        }
    }
}
