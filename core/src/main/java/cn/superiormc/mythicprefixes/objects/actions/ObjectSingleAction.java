package cn.superiormc.mythicprefixes.objects.actions;

import cn.superiormc.mythicprefixes.manager.ActionManager;
import cn.superiormc.mythicprefixes.objects.AbstractSingleRun;
import cn.superiormc.mythicprefixes.objects.ObjectAction;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ObjectSingleAction extends AbstractSingleRun {

    private final ObjectAction action;


    public ObjectSingleAction(ObjectAction action, ConfigurationSection actionSection) {
        super(actionSection);
        this.action = action;
    }

    public void doAction(Player player) {
        ActionManager.actionManager.doAction(this, player);
    }

    public ObjectAction getAction() {
        return action;
    }

}
