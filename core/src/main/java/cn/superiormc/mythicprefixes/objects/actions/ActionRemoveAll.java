package cn.superiormc.mythicprefixes.objects.actions;

import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.objects.ObjectCache;
import org.bukkit.entity.Player;

public class ActionRemoveAll extends AbstractRunAction {

    public ActionRemoveAll() {
        super("removeall");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, Player player) {
        ObjectCache cache = CacheManager.cacheManager.getPlayerCache(player);
        if (cache != null) {
            cache.removeAllActivePrefix(true);
        }
    }
}
