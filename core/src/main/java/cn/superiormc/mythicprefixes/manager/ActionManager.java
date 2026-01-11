package cn.superiormc.mythicprefixes.manager;


import cn.superiormc.mythicprefixes.objects.actions.*;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ActionManager {

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
