package cn.superiormc.mythicprefixes.objects.actions;

import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.objects.buttons.ObjectPrefix;
import org.bukkit.entity.Player;

public class ActionRemoveAll extends AbstractRunAction {

    public ActionRemoveAll() {
        super("removeall");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, Player player) {
        CacheManager.cacheManager.getPlayerCache(player).removeAllActivePrefix(true);
    }
}
