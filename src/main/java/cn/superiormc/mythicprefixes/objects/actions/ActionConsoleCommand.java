package cn.superiormc.mythicprefixes.objects.actions;

import cn.superiormc.mythicprefixes.utils.CommonUtil;
import org.bukkit.entity.Player;

public class ActionConsoleCommand extends AbstractRunAction {

    public ActionConsoleCommand() {
        super("console_command");
        setRequiredArgs("command");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, Player player) {
        CommonUtil.dispatchCommand(singleAction.getString("command", player));
    }
}
