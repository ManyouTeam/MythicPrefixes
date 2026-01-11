package cn.superiormc.mythicprefixes.objects.actions;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import org.bukkit.entity.Player;

public class ActionActionBar extends AbstractRunAction {

    public ActionActionBar() {
        super("action_bar");
        setRequiredArgs("message");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, Player player) {
        String msg = singleAction.getString("message", player);
        MythicPrefixes.methodUtil.sendActionBar(player, msg);
    }
}
