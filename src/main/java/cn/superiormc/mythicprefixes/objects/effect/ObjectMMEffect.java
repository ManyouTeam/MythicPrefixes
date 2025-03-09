package cn.superiormc.mythicprefixes.objects.effect;

import cn.superiormc.mythicprefixes.manager.ErrorManager;
import cn.superiormc.mythicprefixes.utils.SchedulerUtil;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.players.PlayerData;
import io.lumine.mythic.core.skills.stats.StatModifierType;
import io.lumine.mythic.core.skills.stats.StatRegistry;
import io.lumine.mythic.core.skills.stats.StatSource;
import io.lumine.mythic.core.skills.stats.StatType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ObjectMMEffect extends AbstractEffect {

    private final StatModifierType modifier;

    private final ObjectMMEffectSource source;

    private StatType statType;

    public ObjectMMEffect(String id, Player player, ConfigurationSection section) {
        super(id, player, section);
        this.player = player;
        this.modifier = StatModifierType.get(section.getString("modifier-type", "ADD").toUpperCase());
        this.source = new ObjectMMEffectSource(this);
        Optional<StatType> statTypeOptional = MythicBukkit.inst().getStatManager().getStat(section.getString("stat", ""));
        statTypeOptional.ifPresent(type -> this.statType = type);
    }

    @Override
    public void addPlayerStat() {
        if (statType == null || modifier == null) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicPrefixes] §6Warning: Failed to add MythicMobs effect for player. Reason: Config Error");
            return;
        }
        PlayerData profile = MythicBukkit.inst().getPlayerManager().getProfile(player);
        if (profile == null) {
            retryTimes ++;
            if (retryTimes < 3) {
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicPrefixes] §6Warning: Failed to add MythicMobs effect for player " + player.getName() + "," +
                        " don't worry, we will retry later. Retry Times: " + retryTimes + ".");
                SchedulerUtil.runTaskLater(this::addPlayerStat, 20L);
            } else {
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicPrefixes] §cError: Failed to add MythicMobs effect for player " + player.getName() + "," +
                        " if this always happen, try change cache.load-mode option to JOIN in config.yml file, if it only happens sometimes, just ignore this and ask" +
                        " this player equip the prefix again! This because effect plugin load data is slower than MythicPrefixes this times.");
            }
            return;
        }
        StatRegistry stats = profile.getStatRegistry();
        stats.putValue(statType, source, modifier, section.getDouble("value", 0));
        stats.refresh();
    }

    @Override
    public void removePlayerStat() {
        PlayerData profile = MythicBukkit.inst().getPlayerManager().getProfile(player);
        if (profile != null) {
            StatRegistry stats = profile.getStatRegistry();
            stats.removeValue(statType, source);
        }
    }
}

class ObjectMMEffectSource implements StatSource {

    private ObjectMMEffect mmEffect;

    public ObjectMMEffectSource(ObjectMMEffect mmEffect) {
        this.mmEffect = mmEffect;
    }

    public ObjectMMEffect getMmEffect() {
        return mmEffect;
    }
}