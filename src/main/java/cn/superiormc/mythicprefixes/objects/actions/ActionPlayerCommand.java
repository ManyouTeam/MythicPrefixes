package cn.superiormc.mythicprefixes.objects.actions;

import cn.superiormc.mythicprefixes.utils.CommonUtil;
import org.bukkit.entity.Player;

public class ActionPlayerCommand extends AbstractRunAction {

    public ActionPlayerCommand() {
        super("player_command");
        setRequiredArgs("command");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, Player player) {
        CommonUtil.dispatchCommand(player, singleAction.getString("command", player));
    }
}
