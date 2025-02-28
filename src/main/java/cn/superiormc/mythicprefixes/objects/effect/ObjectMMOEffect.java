package cn.superiormc.mythicprefixes.objects.effect;

import cn.superiormc.mythicprefixes.manager.ErrorManager;
import cn.superiormc.mythicprefixes.utils.SchedulerUtil;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.StatMap;
import io.lumine.mythic.lib.api.stat.modifier.StatModifier;
import io.lumine.mythic.lib.player.modifier.ModifierType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ObjectMMOEffect extends AbstractEffect {

    private final StatModifier modifier;

    private final String stat;

    public ObjectMMOEffect(String id, Player player, ConfigurationSection section) {
        super(id, player, section);
        this.player = player;
        this.stat = section.getString("stat", "");
        this.modifier = new StatModifier("MythicPrefixes_" + id, stat, section.getDouble("value", 0), ModifierType.FLAT);
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
