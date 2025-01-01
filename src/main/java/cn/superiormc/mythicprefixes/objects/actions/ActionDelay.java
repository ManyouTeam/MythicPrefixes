package cn.superiormc.mythicprefixes.objects.actions;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.objects.ObjectAction;
import cn.superiormc.mythicprefixes.utils.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ActionDelay extends AbstractRunAction {

    public ActionDelay() {
        super("delay");
        setRequiredArgs("time", "actions");
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
        long time = singleAction.getSection().getLong("time");
        ObjectAction action = new ObjectAction(chanceSection);
        SchedulerUtil.runTaskLater(() -> action.runAllActions(player), time);
    }
}
