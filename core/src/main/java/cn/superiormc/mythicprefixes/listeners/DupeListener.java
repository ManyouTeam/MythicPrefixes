package cn.superiormc.mythicprefixes.listeners;

import cn.superiormc.mythicprefixes.manager.LanguageManager;
import cn.superiormc.mythicprefixes.methods.Dupe;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class DupeListener implements Listener {

    @EventHandler
    public void onCheckChange(PlayerInventorySlotChangeEvent event) {
        ItemStack item = event.getNewItemStack();
        if (Dupe.isGuiDisplayItem(item)) {
            event.getPlayer().getInventory().setItem(event.getSlot(), null);
            LanguageManager.languageManager.sendStringText(event.getPlayer(), "dupe-removed");
        }
    }
}
