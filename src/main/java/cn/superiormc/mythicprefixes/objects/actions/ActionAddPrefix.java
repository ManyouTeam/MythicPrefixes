package cn.superiormc.mythicprefixes.objects.actions;

import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.objects.buttons.ObjectPrefix;
import org.bukkit.entity.Player;

public class ActionAddPrefix extends AbstractRunAction {

    public ActionAddPrefix() {
        super("addprefix");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, Player player) {
        ObjectPrefix prefix = ConfigManager.configManager.getPrefix(singleAction.getString("prefix"));
        if (prefix != null) {
            CacheManager.cacheManager.getPlayerCache(player).addActivePrefix(prefix);
        }
    }
}
