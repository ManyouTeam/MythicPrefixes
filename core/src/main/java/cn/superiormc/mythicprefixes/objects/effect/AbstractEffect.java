package cn.superiormc.mythicprefixes.objects.effect;

import cn.superiormc.mythicprefixes.objects.ObjectCondition;
import cn.superiormc.mythicprefixes.utils.SchedulerUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public abstract class AbstractEffect {

    protected Player player;

    protected final String id;

    protected ConfigurationSection section;

    protected final ObjectCondition condition;

    protected final boolean alwaysCheckCondition;

    protected int retryTimes = 0;

    protected SchedulerUtil retryTask;

    public AbstractEffect(String id, Player player, ConfigurationSection section) {
        this.id = id;
        this.player = player;
        this.section = section;
        this.condition = new ObjectCondition(section.getConfigurationSection("conditions"));
        this.alwaysCheckCondition = section.getBoolean("bypass-condition-after-equip");
    }

    public abstract void addPlayerStat();

    public abstract void removePlayerStat();

    public ObjectCondition getCondition() {
        return condition;
    }

    public boolean isAlwaysCheckCondition() {
        return alwaysCheckCondition;
    }
}
