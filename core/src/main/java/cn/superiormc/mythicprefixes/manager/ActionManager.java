package cn.superiormc.mythicprefixes.manager;


import cn.superiormc.mythicprefixes.objects.actions.AbstractRunAction;
import cn.superiormc.mythicprefixes.objects.actions.ActionActionBar;
import cn.superiormc.mythicprefixes.objects.actions.ActionAddPrefix;
import cn.superiormc.mythicprefixes.objects.actions.ActionAnnouncement;
import cn.superiormc.mythicprefixes.objects.actions.ActionAny;
import cn.superiormc.mythicprefixes.objects.actions.ActionChance;
import cn.superiormc.mythicprefixes.objects.actions.ActionClose;
import cn.superiormc.mythicprefixes.objects.actions.ActionConditional;
import cn.superiormc.mythicprefixes.objects.actions.ActionConsoleCommand;
import cn.superiormc.mythicprefixes.objects.actions.ActionDelay;
import cn.superiormc.mythicprefixes.objects.actions.ActionEffect;
import cn.superiormc.mythicprefixes.objects.actions.ActionEntitySpawn;
import cn.superiormc.mythicprefixes.objects.actions.ActionMessage;
import cn.superiormc.mythicprefixes.objects.actions.ActionOPCommand;
import cn.superiormc.mythicprefixes.objects.actions.ActionParticle;
import cn.superiormc.mythicprefixes.objects.actions.ActionPlayerCommand;
import cn.superiormc.mythicprefixes.objects.actions.ActionRemoveAll;
import cn.superiormc.mythicprefixes.objects.actions.ActionRemovePrefix;
import cn.superiormc.mythicprefixes.objects.actions.ActionSound;
import cn.superiormc.mythicprefixes.objects.actions.ActionTeleport;
import cn.superiormc.mythicprefixes.objects.actions.ActionTitle;
import cn.superiormc.mythicprefixes.objects.actions.ObjectSingleAction;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ActionManager extends AbstractManager {

    public static ActionManager actionManager;

    private Map<String, AbstractRunAction> actions;

    public ActionManager() {
        actionManager = this;
        initActions();
    }

    private void initActions() {
        actions = new HashMap<>();
        registerNewAction("message", new ActionMessage());
        registerNewAction("title", new ActionTitle());
        registerNewAction("action_bar", new ActionActionBar());
        registerNewAction("particle", new ActionParticle());
        registerNewAction("sound", new ActionSound());
        registerNewAction("announcement", new ActionAnnouncement());
        registerNewAction("effect", new ActionEffect());
        registerNewAction("console_command", new ActionConsoleCommand());
        registerNewAction("op_command", new ActionOPCommand());
        registerNewAction("player_command", new ActionPlayerCommand());
        registerNewAction("teleport", new ActionTeleport());
        registerNewAction("entity_spawn", new ActionEntitySpawn());
        registerNewAction("close", new ActionClose());
        registerNewAction("chance", new ActionChance());
        registerNewAction("delay", new ActionDelay());
        registerNewAction("any", new ActionAny());
        registerNewAction("add_prefix", new ActionAddPrefix());
        registerNewAction("remove_prefix", new ActionRemovePrefix());
        registerNewAction("removeall", new ActionRemoveAll());
        registerNewAction("conditional", new ActionConditional());
    }

    public void registerNewAction(String actionID,
                                  AbstractRunAction action) {
        if (!actions.containsKey(actionID)) {
            actions.put(actionID, action);
        }
    }

    public void doAction(ObjectSingleAction action, Player player) {
        for (AbstractRunAction runAction : actions.values()) {
            String type = action.getString("type");
            if (runAction.getType().equals(type)) {
                runAction.runAction(action, player);
            }
        }
    }
}
