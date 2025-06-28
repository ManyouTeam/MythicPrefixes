package cn.superiormc.mythicprefixes.libreforge;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.libreforge.Holder;
import com.willfp.libreforge.SimpleHolder;
import com.willfp.libreforge.ViolationContext;
import com.willfp.libreforge.conditions.ConditionList;
import com.willfp.libreforge.conditions.Conditions;
import com.willfp.libreforge.effects.EffectList;
import com.willfp.libreforge.effects.Effects;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class LibreforgeEffect implements Holder {

    public NamespacedKey namespacedKey;

    private String id;

    private Config config;

    private EffectList effectList;

    private ConditionList conditionList;

    private Holder holder;

    public LibreforgeEffect(String id, Config config) {
        this.id = id;
        this.namespacedKey = new NamespacedKey(MythicPrefixes.instance,
                "mythicprefixes_" + id);
        this.config = config;
        initEffectObject();
    }

    private void initEffectObject() {
        this.effectList = Effects.INSTANCE.compile(config.getSubsections("effects"),
                new ViolationContext(EcoPlugin.getPlugin("eco"), id + " Effects"));
        this.conditionList = Conditions.INSTANCE.compile(config.getSubsections("conditions"),
                new ViolationContext(EcoPlugin.getPlugin("eco"), id + " Effects"));
        this.holder = new SimpleHolder(namespacedKey, effectList, conditionList);
    }

    @NotNull
    @Override
    public ConditionList getConditions() {
        return conditionList;
    }

    @NotNull
    @Override
    public EffectList getEffects() {
        return effectList;
    }

    @NotNull
    @Override
    public NamespacedKey getId() {
        return namespacedKey;
    }

    public Holder getHolder() {
        return holder;
    }

}
