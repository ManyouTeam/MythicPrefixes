package cn.superiormc.mythicprefixes.objects;

import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.objects.buttons.ObjectPrefix;
import cn.superiormc.mythicprefixes.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class ObjectDisplayPlaceholder {

    private final String id;

    private final String startSymbol;

    private final String endSymbol;

    private final String splitSymbol;

    private final List<String> blackPrefixes;

    private final int displayAmount;

    private final String empty;

    public ObjectDisplayPlaceholder(String id, ConfigurationSection section) {
        this.id = id;
        this.startSymbol = TextUtil.parse(section.getString("start-symbol"));
        this.endSymbol = TextUtil.parse(section.getString("end-symbol"));
        this.splitSymbol = TextUtil.parse(section.getString("split-symbol"));
        this.blackPrefixes = section.getStringList("black-prefixes");
        this.displayAmount = section.getInt("display-amount", -1);
        this.empty = section.getString("empty-display", null);
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
        Collection<ObjectPrefix> tempVal5 = cache.getActivePrefixes();
        if (prefix != null) {
            tempVal5.add(prefix);
        }
        for (ObjectPrefix tempVal3 : tempVal5) {
            if (displayAmount > 0 && tempVal4 >= displayAmount) {
                continue;
            }
            if (blackPrefixes.contains(tempVal3.getId())) {
                continue;
            }
            if (!tempVal1.toString().equals(startSymbol)) {
                tempVal1.append(splitSymbol);
            }
            tempVal1.append(tempVal3.getDisplayValue(cache.getPlayer()));
            tempVal4 ++;
        }
        if (tempVal4 == 0 && empty != null) {
            return empty;
        }
        tempVal1.append(endSymbol);
        return tempVal1.toString();
    }

}
