package cn.superiormc.mythicprefixes.gui;

import cn.superiormc.mythicprefixes.gui.InvGUI;
import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.objects.ObjectPrefix;
import cn.superiormc.mythicprefixes.objects.PrefixStatus;
import cn.superiormc.mythicprefixes.utils.ItemUtil;
import cn.superiormc.mythicprefixes.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ChoosePrefixGUI extends InvGUI {

    private Map<Integer, ObjectPrefix> prefixCache = new HashMap<>();

    private List<Integer> slotCache = new ArrayList<>();

    private int needPages = 1;

    private int nowPage = 1;

    private int nextPageSlot = -1;

    private int previousPageSlot = -1;

    public ChoosePrefixGUI(Player player) {
        super(player);
        slotCache = ConfigManager.configManager.getIntList("choose-prefix-gui.prefix-item-slot");
        constructGUI();
    }

    @Override
    protected void constructGUI() {
        int i = 0;
        for (ObjectPrefix shop : ConfigManager.configManager.getPrefixesWithoutHide()) {
            prefixCache.put(i, shop);
            i ++;
        }
        if (prefixCache.size() >= slotCache.size()) {
            needPages = (int) (Math.ceil((double) prefixCache.size() / slotCache.size()));
        }
        if (Objects.isNull(inv)) {
            inv = Bukkit.createInventory(player, ConfigManager.configManager.getInt("choose-prefix-gui.size", 54),
                    TextUtil.parse(ConfigManager.configManager.getString("choose-prefix-gui." +
                            "title")));
        }
        for (int c = 0 ; c < slotCache.size() ; c ++) {
            ObjectPrefix prefix = prefixCache.get((nowPage - 1)  * slotCache.size() + c);
            if (prefix == null) {
                continue;
            }
            ItemStack prefixItem = prefix.getDisplayItem(player);
            inv.setItem(c, prefixItem);
        }
        ConfigurationSection nextPageSection = ConfigManager.configManager.getSection().
                getConfigurationSection("choose-prefix-gui.next-page-item");
        if (nowPage < needPages && nextPageSection != null) {
            ItemStack nextPageItem = ItemUtil.buildItemStack(player, nextPageSection,
                    "max", String.valueOf(needPages),
                    "now", String.valueOf(nowPage));
            nextPageSlot = nextPageSection.getInt("slot", 52);
            inv.setItem(nextPageSlot, nextPageItem);
        } else {
            if (nextPageSlot >= 0 && nextPageSlot < 54) {
                inv.clear(nextPageSlot);
            }
        }
        ConfigurationSection previousPageSection = ConfigManager.configManager.getSection().
                getConfigurationSection("choose-prefix-gui.previous-page-item");
        if (nowPage > 1 && previousPageSection != null) {
            ItemStack previousPageItem = ItemUtil.buildItemStack(player, previousPageSection,
                    "max", String.valueOf(needPages),
                    "now", String.valueOf(nowPage));
            previousPageSlot = previousPageSection.getInt("slot", 46);
            inv.setItem(previousPageSlot, previousPageItem);
        } else {
            if (previousPageSlot >= 0 && previousPageSlot < 54) {
                inv.clear(previousPageSlot);
            }
        }
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        if (slotCache.contains(slot)) {
            ObjectPrefix prefix = prefixCache.get(slot);
            if (prefix != null) {
                switch (prefix.getConditionMeet(player)) {
                    case CAN_USE:
                        CacheManager.cacheManager.getPlayerCache(player).addActivePrefix(prefix);
                        break;
                    case USING:
                        CacheManager.cacheManager.getPlayerCache(player).removeActivePrefix(prefix);
                        break;
                }
            }
        }
        else if (slot == nextPageSlot) {
            if (nowPage < needPages) {
                nowPage++;
            }
        }
        else if (slot == previousPageSlot) {
            if (nowPage > 0) {
                nowPage--;
            }
        }
        constructGUI();
        return true;
    }
}
