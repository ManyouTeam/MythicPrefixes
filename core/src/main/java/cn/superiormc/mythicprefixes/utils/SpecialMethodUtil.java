package cn.superiormc.mythicprefixes.utils;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public interface SpecialMethodUtil {

    String methodID();

    void dispatchCommand(String command);

    void dispatchCommand(Player player, String command);

    void dispatchOpCommand(Player player, String command);

    void spawnEntity(Location location, EntityType entity);

    void playerTeleport(Player player, Location location);

    SkullMeta setSkullMeta(SkullMeta meta, String skull);

    void setItemName(ItemMeta meta, String name, Player player);

    void setItemLore(ItemMeta meta, List<String> lore, Player player);

    void sendChat(Player player, String text);

    void sendTitle(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut);

    void sendActionBar(Player player, String message);

    void sendBossBar(Player player,
                     String title,
                     float progress,
                     String color,
                     String style);

    Inventory createNewInv(Player player, int size, String text);

    String legacyParse(String text);

    String getItemName(ItemMeta meta);
}
