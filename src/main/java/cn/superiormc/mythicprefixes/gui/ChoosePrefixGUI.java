package cn.superiormc.mythicprefixes.gui;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.objects.PrefixStatus;
import cn.superiormc.mythicprefixes.objects.buttons.AbstractButton;
import cn.superiormc.mythicprefixes.objects.buttons.ObjectPrefix;
import cn.superiormc.mythicprefixes.utils.ItemUtil;
import cn.superiormc.mythicprefixes.utils.TextUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ChoosePrefixGUI extends InvGUI {

    private Map<Integer, AbstractButton> prefixCache = new HashMap<>();

    private Map<Integer, AbstractButton> buttonCache = new HashMap<>();

    private List<Integer> slotCache;

    private Filter filter = Filter.ALL;

    private int needPages = 1;

    private int nowPage = 1;

    private int nextPageSlot = -1;

    private int previousPageSlot = -1;

    private int filterSlot = -1;

    public ChoosePrefixGUI(Player player) {
        super(player);
        slotCache = ConfigManager.configManager.getIntList("choose-prefix-gui.prefix-item-slot");
        constructGUI();
    }

    @Override
    protected void constructGUI() {
        int i = 0;
        if (prefixCache.isEmpty()) {
            for (ObjectPrefix prefix : ConfigManager.configManager.getPrefixesWithoutHide()) {
                PrefixStatus status = prefix.getConditionMeet(player);
                if ((filter == Filter.ALL || (filter == Filter.USING && status == PrefixStatus.USING) || (
                        filter == Filter.CAN_USE && status == PrefixStatus.CAN_USE))
                        && prefix.shouldHideInGUI(player)) {
                    prefixCache.put(i, prefix);
                    i++;
                }
            }
            if (prefixCache.size() >= slotCache.size()) {
                needPages = (int) (Math.ceil((double) prefixCache.size() / slotCache.size()));
            }
        }
        if (Objects.isNull(inv)) {
            int size = ConfigManager.configManager.getInt("choose-prefix-gui.size", 54);
            String title = ConfigManager.configManager.getString("choose-prefix-gui." +
                            "title", "Tag GUI");
            if (MythicPrefixes.isPaper && ConfigManager.configManager.getBoolean("paper-api.use-component.menu-title")) {
                inv = Bukkit.createInventory(player, size, MiniMessage.miniMessage().deserialize(TextUtil.withPAPI(title, player)));
            } else {
                inv = Bukkit.createInventory(player, size, TextUtil.parse(title, player));
            }
        }
        for (int c = 0 ; c < slotCache.size() ; c ++) {
            AbstractButton prefix = prefixCache.get((nowPage - 1)  * slotCache.size() + c);
            if (prefix == null) {
                inv.clear(slotCache.get(c));
                continue;
            }
            inv.setItem(slotCache.get(c), prefix.getDisplayItem(player));
        }
        if (buttonCache.isEmpty()) {
            for (int slot : ConfigManager.configManager.getButtons().keySet()) {
                AbstractButton button = ConfigManager.configManager.getButtons().get(slot);
                if (button == null) {
                    continue;
                }
                buttonCache.put(slot, button);
                inv.setItem(slot, button.getDisplayItem(player));
            }
        }
        ConfigurationSection nextPageSection = ConfigManager.configManager.getConfigurationSection("choose-prefix-gui.next-page-item");
        if (nowPage < needPages && nextPageSection != null) {
            ItemStack nextPageItem = ItemUtil.buildItemStack(player, nextPageSection,
                    "max", String.valueOf(needPages),
                    "now", String.valueOf(nowPage));
            nextPageSlot = nextPageSection.getInt("slot", 52);
            if (nextPageSlot >= 0) {
                inv.setItem(nextPageSlot, nextPageItem);
            }
        } else {
            if (nextPageSlot >= 0 && nextPageSlot < 54) {
                inv.clear(nextPageSlot);
            }
        }
        ConfigurationSection previousPageSection = ConfigManager.configManager.getConfigurationSection("choose-prefix-gui.previous-page-item");
        if (nowPage > 1 && previousPageSection != null) {
            ItemStack previousPageItem = ItemUtil.buildItemStack(player, previousPageSection,
                    "max", String.valueOf(needPages),
                    "now", String.valueOf(nowPage));
            previousPageSlot = previousPageSection.getInt("slot", 46);
            if (previousPageSlot >= 0) {
                inv.setItem(previousPageSlot, previousPageItem);
            }
        } else {
            if (previousPageSlot >= 0 && previousPageSlot < 54) {
                inv.clear(previousPageSlot);
            }
        }
        ConfigurationSection filterSection = ConfigManager.configManager.getConfigurationSection("choose-prefix-gui.filter-item");
        if (filterSection != null) {
            String filterPlaceholder = filterSection.getString("placeholder.all");
            if (filter == Filter.USING) {
                filterPlaceholder = filterSection.getString("placeholder.using");
            } else if (filter == Filter.CAN_USE) {
                filterPlaceholder = filterSection.getString("placeholder.can-use");
            }
            ItemStack filterItem = ItemUtil.buildItemStack(player, filterSection,
                    "filter", filterPlaceholder);
            filterSlot = filterSection.getInt("slot", 47);
            if (filterSlot >= 0) {
                inv.setItem(filterSlot, filterItem);
            }
        }
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        if (slot == nextPageSlot) {
            if (nowPage < needPages) {
                nowPage++;
            }
        } else if (slot == previousPageSlot) {
            if (nowPage > 0) {
                nowPage--;
            }
        } else if (slot == filterSlot) {
            if (filter == Filter.ALL) {
                filter = Filter.USING;
            } else if (filter == Filter.USING) {
                filter = Filter.CAN_USE;
            } else if (filter == Filter.CAN_USE) {
                filter = Filter.ALL;
            }
            prefixCache.clear();
        } else {
            AbstractButton prefix = prefixCache.get((nowPage - 1)  * slotCache.size() + slotCache.indexOf(slot));
            if (prefix != null) {
                prefix.clickEvent(type, player);
            } else {
                AbstractButton button = buttonCache.get(slot);
                if (button != null) {
                    button.clickEvent(type, player);
                }
            }
        }
        constructGUI();
        return true;
    }
}
