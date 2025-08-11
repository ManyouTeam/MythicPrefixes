package cn.superiormc.mythicprefixes.objects.effect;

import java.util.Collection;
import java.util.HashSet;

public class EffectStatus {

    private Collection<AbstractEffect> activedEffects = new HashSet<>();

    private Collection<AbstractEffect> notActivedEffects = new HashSet<>();

    public EffectStatus() {
        //Empty...
    }

    public void addAcvtiedEffects(AbstractEffect effect) {
        notActivedEffects.remove(effect);
        activedEffects.add(effect);
    }

    public void addNotActivedEffects(AbstractEffect effect) {
        activedEffects.remove(effect);
        notActivedEffects.add(effect);
    }

    public Collection<AbstractEffect> getActivedEffects() {
        return activedEffects;
    }

    public Collection<AbstractEffect> getNotActivedEffects() {
        return notActivedEffects;
    }

    public void retryActiveEffects() {
        for (AbstractEffect effect : notActivedEffects) {
            if (effect.getCondition().getAllBoolean(effect.player)) {
                effect.addPlayerStat();
                addAcvtiedEffects(effect);
            }
        }
        for (AbstractEffect effect : activedEffects) {
            if (!effect.isAlwaysCheckCondition() && !effect.getCondition().getAllBoolean(effect.player)) {
                effect.removePlayerStat();
                addNotActivedEffects(effect);
            }
        }
    }
}
