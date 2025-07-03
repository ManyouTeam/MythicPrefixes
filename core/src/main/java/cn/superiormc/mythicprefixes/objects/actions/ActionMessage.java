package cn.superiormc.mythicprefixes.objects.actions;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import org.bukkit.entity.Player;

public class ActionMessage extends AbstractRunAction {

    public ActionMessage() {
        super("message");
        setRequiredArgs("message");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, Player player) {
        MythicPrefixes.methodUtil.sendMessage(player, singleAction.getString("message", player));
    }
}
