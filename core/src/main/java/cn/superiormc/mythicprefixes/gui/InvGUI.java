package cn.superiormc.mythicprefixes.gui;

import cn.superiormc.mythicprefixes.methods.Dupe;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.manager.MenuStatusManager;
import cn.superiormc.mythicprefixes.utils.SchedulerUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public abstract class InvGUI extends AbstractGUI implements InventoryHolder {

    protected Inventory inv;

    public String title;

    protected SchedulerUtil updateTask;

    public InvGUI(Player player) {
        super(player);
    }

    public abstract boolean clickEventHandle(Inventory inventory, ClickType type, int slot);

    @Override
    public void openGUI(boolean reopen) {
        GUIStatus previousStatus = MenuStatusManager.menuStatusManager.getGUIStatus(player);
        if (!MenuStatusManager.menuStatusManager.canOpenGUI(player, this, reopen)) {
            return;
        }
        constructGUI();
        if (inv != null) {
            SchedulerUtil.runSync(player, () -> player.openInventory(inv));
            if (ConfigManager.configManager.getBoolean("choose-prefix-gui.menu-update.circle-update")) {
                updateTask = SchedulerUtil.runTaskTimer(() -> SchedulerUtil.runSync(player, () -> {
                    if (MenuStatusManager.menuStatusManager.getOpeningGUI(player) == this) {
                        constructGUI();
                    }
                }), 20L, 20L);
            }
        } else {
            MenuStatusManager.menuStatusManager.setGUIStatus(player, previousStatus);
        }
    }

    @Override
    public void closeGUI() {
        player.closeInventory();
    }

    public boolean getChangeable() {
        return false;
    }

    public boolean closeEventHandle(Inventory inventory) {
        if (updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }
        return true;
    }

    public boolean dragEventHandle(Map<Integer, ItemStack> newItems) {
        return true;
    }

    public void afterClickEventHandle(ItemStack cursor, ItemStack currentItem, int slot) {
    }

    public @NonNull Inventory getInventory() {
        return inv;
    }

    public void setItem(int slot, ItemStack item) {
        inv.setItem(slot, Dupe.markGuiDisplayItem(item));
    }

    public ConfigurationSection getSection() {
        return null;
    }
}
