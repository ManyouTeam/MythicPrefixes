package cn.superiormc.mythicprefixes.objects;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.objects.buttons.ObjectPrefix;
import cn.superiormc.mythicprefixes.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

public class ObjectDisplayPlaceholder {

    private final String id;

    private final String startSymbol;

    private final String endSymbol;

    private final String splitSymbol;

    private final List<String> blackPrefixes;

    private final int displayAmount;

    private final String empty;

    public Collection<ObjectPrefix> defaultPrefixCaches = new TreeSet<>();

    public ObjectDisplayPlaceholder(String id, ConfigurationSection section) {
        this.id = id;
        this.startSymbol = TextUtil.parse(section.getString("start-symbol"));
        this.endSymbol = TextUtil.parse(section.getString("end-symbol"));
        this.splitSymbol = TextUtil.parse(section.getString("split-symbol"));
        this.blackPrefixes = section.getStringList("black-prefixes");
        this.displayAmount = section.getInt("display-amount", -1);
        this.empty = section.getString("empty-display", null);
        for (String prefix : section.getStringList("default-prefixes")) {
            ObjectPrefix objectPrefix = ConfigManager.configManager.getPrefix(prefix);
            if (objectPrefix != null && !MythicPrefixes.freeVersion) {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicPrefixes] §fSet prefix " + prefix + " as default prefix, " +
                        "it will no longer display in tag GUI!");
                defaultPrefixCaches.add(objectPrefix);
                objectPrefix.setDefaultPrefix(true);
            }
        }
    }

    public String getId() {
        return id;
    }

    public String getDisplayText(ObjectCache cache) {
        return getDisplayText(cache, null);
    }

    public String getDisplayText(ObjectCache cache, ObjectPrefix prefix) {
        StringBuilder tempVal1 = new StringBuilder(startSymbol);
        int tempVal4 = 0;
        Collection<ObjectPrefix> tempVal5 = new ArrayList<>();
        if (prefix != null) {
            tempVal5.add(prefix);
            tempVal4 ++;
        }
        for (ObjectPrefix tempVal3 : cache.getActivePrefixes()) {
            if (displayAmount > 0 && tempVal4 >= displayAmount) {
                continue;
            }
            if (blackPrefixes.contains(tempVal3.getId())) {
                continue;
            }
            tempVal5.add(tempVal3);
            tempVal4 ++;
        }
        if (tempVal5.isEmpty()) {
            for (ObjectPrefix defaultPrefix : defaultPrefixCaches) {
                if (defaultPrefix.getPrefixStatus(cache).equals(PrefixStatus.CAN_USE)) {
                    tempVal5.add(defaultPrefix);
                    tempVal4 ++;
                    break;
                }
            }
        }
        for (ObjectPrefix tempVal3 : tempVal5) {
            if (!tempVal1.toString().equals(startSymbol)) {
                tempVal1.append(splitSymbol);
            }
            tempVal1.append(tempVal3.getDisplayValue(cache.getPlayer()));
        }
        if (tempVal4 == 0 && empty != null) {
            return empty;
        }
        tempVal1.append(endSymbol);
        return tempVal1.toString();
    }

}
