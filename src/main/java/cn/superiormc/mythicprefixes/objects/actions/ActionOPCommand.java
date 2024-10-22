package cn.superiormc.mythicprefixes.objects.actions;

import cn.superiormc.mythicprefixes.utils.CommonUtil;
import org.bukkit.entity.Player;

public class ActionOPCommand extends AbstractRunAction {

    public ActionOPCommand() {
        super("op_command");
        setRequiredArgs("command");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, Player player) {
        CommonUtil.dispatchOpCommand(player, singleAction.getString("command", player));
    }
}