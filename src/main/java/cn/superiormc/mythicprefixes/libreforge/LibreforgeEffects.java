package cn.superiormc.mythicprefixes.libreforge;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.objects.buttons.ObjectPrefix;
import cn.superiormc.mythicprefixes.api.MythicPrefixesAPI;
import com.willfp.eco.core.config.ConfigType;
import com.willfp.eco.core.config.Configs;
import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.libreforge.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class LibreforgeEffects {

    public static LibreforgeEffects libreforgeEffects;

    private Config config;

    private Map<String, LibreforgeEffect> libreforgeEffectMap = new HashMap<>();
    public LibreforgeEffects() {
        libreforgeEffects = this;
        initLibreforgeHook();
    }

    private void initLibreforgeHook() {
        HolderProviderKt.registerHolderProvider(new HolderProvider() {
            @NotNull
            @Override
            public Collection<ProvidedHolder> provide(@NotNull Dispatcher<?> dispatcher) {
                Collection<ProvidedHolder> tempVal1 = new HashSet<>();
                if (dispatcher.getDispatcher() instanceof Player) {
                    Player player = (Player) dispatcher.getDispatcher();
                    for (ObjectPrefix tempVal2: MythicPrefixesAPI.getActivedPrefixes(player)) {
                        LibreforgeEffect tempVal3 = libreforgeEffectMap.get(tempVal2.getId());
                        if (tempVal3 == null) {
                            continue;
                        }
                        tempVal1.add(new SimpleProvidedHolder(tempVal3.getHolder()));
                    }
                }
                return tempVal1;
            }
        });
    }

    public void registerLibreforgeEffect(String id) {
        File file = new File(MythicPrefixes.instance.getDataFolder(), "config.yml");
        if (!file.exists()) {
            return;
        }
        this.config = Configs.fromFile(file, ConfigType.YAML);
        for (Config tempVal1 : config.getSubsections("libreforge-effects")) {
            String tempVal2 = tempVal1.getString("id");
            if (id.equals(tempVal2)) {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicPrefixes] §fSuccessfully added " + id + " " +
                        "effects to libreforge!");
                libreforgeEffectMap.put(id,
                new LibreforgeEffect(id, tempVal1));
                break;
            }
        }
    }
}
