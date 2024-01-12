package cn.superiormc.mythicprefixes.objects;

import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ObjectDisplayPlaceholder {

    private final String id;

    private final String startSymbol;

    private final String endSymbol;

    private final String splitSymbol;

    private final List<String> blackPrefixes;

    private final int displayAmount;


    public ObjectDisplayPlaceholder(String id, ConfigurationSection section) {
        this.id = id;
        this.startSymbol = TextUtil.parse(section.getString("start-symbol"));
        this.endSymbol = TextUtil.parse(section.getString("end-symbol"));
        this.splitSymbol = TextUtil.parse(section.getString("split-symbol"));
        this.blackPrefixes = section.getStringList("black-prefixes");
        this.displayAmount = section.getInt("display-amount", -1);
    }

    public String getId() {
        return id;
    }

    public String getDisplayText(Player player) {
        StringBuilder tempVal1 = new StringBuilder(startSymbol);
        int tempVal4 = 0;
        ObjectCache tempVal2 = CacheManager.cacheManager.getPlayerCache(player);
        for (ObjectPrefix tempVal3 : tempVal2.getActivePrefixes()) {
            if (displayAmount > 0 && tempVal4 >= displayAmount) {
                continue;
            }
            if (blackPrefixes.contains(tempVal3.getId())) {
                continue;
            }
            if (!tempVal1.toString().equals(startSymbol)) {
                tempVal1.append(splitSymbol);
            }
            tempVal1.append(tempVal3.getDisplayValue(player));
            tempVal4 ++;
        }
        tempVal1.append(endSymbol);
        return tempVal1.toString();
    }

}
