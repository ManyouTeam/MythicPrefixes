package cn.superiormc.mythicprefixes.objects.actions;

import org.bukkit.entity.Player;

public class ActionClose extends AbstractRunAction {

    public ActionClose() {
        super("close");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, Player player) {
        player.closeInventory();
    }
}
