package cn.superiormc.mythicprefixes.objects.actions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ActionAnnouncement extends AbstractRunAction {

    public ActionAnnouncement() {
        super("announcement");
        setRequiredArgs("message");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, Player player) {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        for (Player p : players) {
            p.sendMessage(singleAction.getString("message", player));
        }
    }
}
