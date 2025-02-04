package cn.superiormc.mythicprefixes.objects;

import cn.superiormc.mythicprefixes.manager.ErrorManager;
import cn.superiormc.mythicprefixes.utils.SchedulerUtil;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.StatMap;
import io.lumine.mythic.lib.api.stat.modifier.StatModifier;
import io.lumine.mythic.lib.player.modifier.ModifierType;
import org.bukkit.OfflinePlayer;

public class ObjectMMOEffect {

    private StatModifier modifier = null;

    private final String id;

    private final OfflinePlayer player;

    private final String stat;

    private int retryTimes = 0;

    private SchedulerUtil retryTask;

    public ObjectMMOEffect(String id, OfflinePlayer player, String stat, double value) {
        this.id = id;
        this.player = player;
        this.stat = stat;
        this.modifier = new StatModifier("MythicPrefixes_" + id, stat, value, ModifierType.FLAT);
    }

    public void addPlayerStat() {
        try {
            if (MMOPlayerData.has(player)) {
                MMOPlayerData playerData = MMOPlayerData.get(player);
                StatMap statMap = playerData.getStatMap();
                modifier.register(playerData);
                statMap.getInstance(stat).addModifier(modifier);
            } else {
                retryTimes ++;
                if (retryTimes < 3) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicPrefixes] §6Warning: Failed to add MMO effect for player " + player.getName() + "," +
                        " don't worry, we will retry later. Retry Times: " + retryTimes + ".");
                    SchedulerUtil.runTaskLater(this::addPlayerStat, 20L);
                } else {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicPrefixes] §cError: Failed to add MMO effect for player " + player.getName() + "," +
                            " if this always happen, try change cache.load-mode option to JOIN in config.yml file, if it only happens sometimes, just ignore this and ask" +
                            " this player equip the prefix again! This because MMO load data is slower than MythicPrefixes this times.");
                }
            }
        } catch (Throwable throwable) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicPrefixes] §cError: Failed to add MMO effect for player " + player.getName() + "," +
                    " if this always happen, try change cache.load-mode option to JOIN in config.yml file, if it only happens sometimes, just ignore this and ask" +
                    " this player equip the prefix again! This because MMO load data is slower than MythicPrefixes this times.");
        }
    }

    public void removePlayerStat() {
        retryTimes = 0;
        if (retryTask != null) {
            retryTask.cancel();
            retryTask = null;
        }
        if (player.isOnline()) {
          MMOPlayerData playerData = MMOPlayerData.get(player);
          StatMap statMap = playerData.getStatMap();
          statMap.getInstance(stat).remove("MythicPrefixes_" + id);
        }
    }
}
