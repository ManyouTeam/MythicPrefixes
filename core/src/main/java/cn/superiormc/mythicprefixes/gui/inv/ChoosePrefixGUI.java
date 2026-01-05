package cn.superiormc.mythicprefixes.gui.inv;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.gui.Filter;
import cn.superiormc.mythicprefixes.gui.InvGUI;
import cn.superiormc.mythicprefixes.gui.form.FormChoosePrefixGUI;
import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.objects.ObjectCache;
import cn.superiormc.mythicprefixes.objects.PrefixStatus;
import cn.superiormc.mythicprefixes.objects.buttons.AbstractButton;
import cn.superiormc.mythicprefixes.objects.buttons.ObjectPrefix;
import cn.superiormc.mythicprefixes.utils.CommonUtil;
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

    private final Map<Integer, AbstractButton> prefixCache = new HashMap<>();

    private final Map<Integer, AbstractButton> buttonCache = new HashMap<>();

    private final List<Integer> slotCache;

    private Filter filter = Filter.ALL;

    private int needPages = 1;

    private int nowPage = 1;

    private int nextPageSlot = -1;

    private int previousPageSlot = -1;

    private int filterSlot = -1;

    private final String selectedGroup;

    private ChoosePrefixGUI(Player player, String group) {
        super(player);
        selectedGroup = group;
        slotCache = ConfigManager.configManager.getIntList("choose-prefix-gui.prefix-item-slot");
        constructGUI();
    }

    @Override
    protected void constructGUI() {
        int i = 0;
        ObjectCache cache = CacheManager.cacheManager.getPlayerCache(player);
        if (prefixCache.isEmpty()) {
            for (ObjectPrefix prefix : ConfigManager.configManager.getPrefixesWithoutHide()) {
                if (!selectedGroup.equals("all") && !prefix.getGroups().contains(selectedGroup)) {
                    continue;
                }
                PrefixStatus status = prefix.getPrefixStatus(cache);
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
        title = TextUtil.withPAPI(ConfigManager.configManager.getString(player,"choose-prefix-gui." +
                "title", "Tag GUI", "now", String.valueOf(nowPage), "max", String.valueOf(needPages)), player);
        if (Objects.isNull(inv)) {
            int size = ConfigManager.configManager.getInt("choose-prefix-gui.size", 54);
            inv = MythicPrefixes.methodUtil.createNewInv(player, size, title);
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
        if (filterSection != null && !MythicPrefixes.freeVersion) {
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
            constructGUI();
            return true;
        }

        if (slot == previousPageSlot) {
            if (nowPage > 1) {
                nowPage--;
            }
            constructGUI();
            return true;
        }

        if (slot == filterSlot && !MythicPrefixes.freeVersion) {
            if (filter == Filter.ALL) filter = Filter.USING;
            else if (filter == Filter.USING) filter = Filter.CAN_USE;
            else if (filter == Filter.CAN_USE) filter = Filter.ALL;

            prefixCache.clear();
            constructGUI();
            return true;
        }

        int prefixIndexInSlots = slotCache.indexOf(slot);
        if (prefixIndexInSlots != -1) { // 是称号槽位
            int index = (nowPage - 1) * slotCache.size() + prefixIndexInSlots;
            AbstractButton prefix = prefixCache.get(index);
            if (prefix != null) {
                prefix.clickEvent(type, player);
                constructGUI();
                return true;
            }
        }

        AbstractButton button = buttonCache.get(slot);
        if (button != null) {
            button.clickEvent(type, player);
            constructGUI();
            return true;
        }

        return true;
    }

    public static void openGUI(Player player, String group) {
        if (CommonUtil.isBedrockPlayer(player)) {
            FormChoosePrefixGUI gui = new FormChoosePrefixGUI(player);
            gui.openGUI();
            return;
        }
        ChoosePrefixGUI gui = new ChoosePrefixGUI(player, group);
        gui.openGUI();
    }
}
