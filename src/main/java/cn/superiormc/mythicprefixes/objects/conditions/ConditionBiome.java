package cn.superiormc.mythicprefixes.objects.conditions;

import org.bukkit.entity.Player;

public class ConditionBiome extends AbstractCheckCondition {

    public ConditionBiome() {
        super("biome");
        setRequiredArgs("biome");
    }

    @Override
    protected boolean onCheckCondition(ObjectSingleCondition singleCondition, Player player) {
        return player.getLocation().getBlock().getBiome().name().equals(singleCondition.getString("biome").toUpperCase());
    }
}
