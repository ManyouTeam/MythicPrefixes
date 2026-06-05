package cn.superiormc.mythicprefixes.listeners;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.gui.InvGUI;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.manager.ErrorManager;
import cn.superiormc.mythicprefixes.manager.ListenerManager;
import cn.superiormc.mythicprefixes.utils.PacketInventoryUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.util.Objects;

public class GUIListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player player) {
            try {
                InvGUI gui = ListenerManager.listenerManager.getInvGUI(player);
                if (gui == null) {
                    return;
                }
                if (!e.getView().getTopInventory().equals(gui.getInv())) {
                    player.closeInventory();
                    ListenerManager.listenerManager.unregisterListeners(player);
                    ErrorManager.errorManager.sendErrorMessage("§cError: Found unregistered GUI Listener, now force close the inventory and then delete the excess GUI Listener. If this always heppens, please report to the plugin author.");
                    return;
                }
                if (!Objects.equals(e.getClickedInventory(), gui.getInv())) {
                    if (e.getClick().isShiftClick() || e.getClick() == ClickType.DOUBLE_CLICK || ConfigManager.configManager.getBoolean("choose-prefix-gui.forbid-click-outside")) {
                        e.setCancelled(true);
                    }
                    return;
                }
                if (e.getClick() == ClickType.DOUBLE_CLICK) {
                    e.setCancelled(true);
                    return;
                }
                if (gui.clickEventHandle(e.getClickedInventory(), e.getClick(), e.getSlot())) {
                    e.setCancelled(true);
                }
                if (e.getClick().toString().equals("SWAP_OFFHAND") && e.isCancelled()) {
                    player.getInventory().setItemInOffHand(player.getInventory().getItemInOffHand());
                }
                if (MythicPrefixes.usePacketEvents) {
                    PacketInventoryUtil.packetInventoryUtil.updateTitle(player, gui);
                }
            } catch (Throwable throwable) {
                ErrorManager.errorManager.sendErrorMessage("§cError: Your menu configs has wrong, error message: " +
                        throwable.getMessage());
                throwable.printStackTrace();
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getWhoClicked() instanceof Player player) {
            InvGUI gui = ListenerManager.listenerManager.getInvGUI(player);
            if (gui == null) {
                return;
            }
            if (!e.getView().getTopInventory().equals(gui.getInv())) {
                player.closeInventory();
                ListenerManager.listenerManager.unregisterListeners(player);
                ErrorManager.errorManager.sendErrorMessage("§cError: Found unregistered GUI Listener, now force close the inventory and then delete the excess GUI Listener. If this always heppens, please report to the plugin author.");
                return;
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player player) {
            InvGUI gui = ListenerManager.listenerManager.getInvGUI(player);
            if (gui == null) {
                return;
            }
            if (!Objects.equals(e.getInventory(), gui.getInv())) {
                return;
            }
            ListenerManager.listenerManager.unregisterNewGUIListener(player, gui);
            if (MythicPrefixes.usePacketEvents) {
                PacketInventoryUtil.packetInventoryUtil.clear(player);
            }
        }
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent e){
        if (ListenerManager.listenerManager.getInvGUI(e.getPlayer()) != null) {
            e.setCancelled(true);
        }
    }
}
