package cn.superiormc.mythicprefixes.utils;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.manager.ErrorManager;
import com.google.common.base.Enums;
import com.google.common.collect.MultimapBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemUtil {

    public static ItemStack buildItemStack(@NotNull Player player, @Nullable ConfigurationSection section, String... args) {
        ItemStack item = new ItemStack(Material.STONE);
        if (section == null) {
            ErrorManager.errorManager.sendErrorMessage("§cError: Can not parse item because the " +
                    "config section is null.");
            return item;
        }
        String materialKey = section.getString("material");
        if (materialKey != null) {
            Material material = Material.getMaterial(materialKey.toUpperCase());
            if (material != null) {
                item.setType(material);
            }
        }
        int amountKey = section.getInt("amount", -1);
        if (amountKey > 0) {
            item.setAmount(amountKey);
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        // Name
        String displayNameKey = section.getString("name");
        if (displayNameKey != null) {
            MythicPrefixes.methodUtil.setItemName(meta, CommonUtil.modifyString(player, displayNameKey, args), player);
        }

        // Lore
        List<String> loreKey = section.getStringList("lore");
        if (!loreKey.isEmpty()) {
            MythicPrefixes.methodUtil.setItemLore(meta, CommonUtil.modifyList(player, loreKey, args), player);
        }

        // Custom Model Data
        if (CommonUtil.getMajorVersion(14)) {
            int customModelDataKey = section.getInt("custom-model-data", section.getInt("cmd", -1));
            if (customModelDataKey > 0) {
                meta.setCustomModelData(customModelDataKey);
            }
        }

        // Flags
        List<String> itemFlagKey = section.getStringList("flags");
        if (!itemFlagKey.isEmpty()) {
            for (String flag : itemFlagKey) {
                flag = flag.toUpperCase();
                ItemFlag itemFlag = Enums.getIfPresent(ItemFlag.class, flag).orNull();
                if (itemFlag != null) {
                    meta.addItemFlags(itemFlag);
                }
                if (CommonUtil.getMinorVersion(20, 6) && itemFlag == ItemFlag.HIDE_ATTRIBUTES && meta.getAttributeModifiers() == null) {
                    meta.setAttributeModifiers(MultimapBuilder.hashKeys().hashSetValues().build());
                }
            }
        }

        // Enchants
        ConfigurationSection enchantsKey = section.getConfigurationSection("enchants");
        if (enchantsKey != null) {
            for (String ench : enchantsKey.getKeys(false)) {
                Enchantment vanillaEnchant = Enchantment.getByKey(NamespacedKey.minecraft(ench.toLowerCase()));
                if (vanillaEnchant != null) {
                    meta.addEnchant(vanillaEnchant, enchantsKey.getInt(ench), true);
                }
            }
        }

        // Glow
        if (CommonUtil.getMinorVersion(20, 5)) {
            if (section.get("glow") != null) {
                meta.setEnchantmentGlintOverride(section.getBoolean("glow"));
            }
        }

        if (CommonUtil.getMinorVersion(21, 2) && !MythicPrefixes.freeVersion) {
            // Item Model
            String itemModel = section.getString("item-model", null);
            if (itemModel != null) {
                meta.setItemModel(CommonUtil.parseNamespacedKey(itemModel));
            }

            // Tooltip Style
            String tooltipStyle = section.getString("tooltip-style", null);
            if (tooltipStyle != null) {
                meta.setTooltipStyle(CommonUtil.parseNamespacedKey(tooltipStyle));
            }
        }

        // Skull
        if (meta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) meta;
            String skullTextureNameKey = section.getString("skull-meta", section.getString("skull"));
            if (skullTextureNameKey != null) {
                MythicPrefixes.methodUtil.setSkullMeta(skullMeta, skullTextureNameKey);
            }
        }
        item.setItemMeta(meta);
        return item;
    }

    // No Color Code
    public static String getItemNameWithoutVanilla(ItemStack displayItem) {
        if (displayItem == null || displayItem.getItemMeta() == null) {
            return "";
        }
        if (displayItem.getItemMeta().hasDisplayName()) {
            return MythicPrefixes.methodUtil.getItemName(displayItem.getItemMeta());
        }
        if (MythicPrefixes.methodUtil.methodID().equals("paper") && ConfigManager.configManager.getBoolean("choose-prefix-gui.auto-translate-item-name")) {
            return "<lang:" + displayItem.translationKey() + ">";
        }
        StringBuilder result = new StringBuilder();
        for (String word : displayItem.getType().name().toLowerCase().split("_")) {
            if (!word.isEmpty()) {
                char firstChar = Character.toUpperCase(word.charAt(0));
                String restOfWord = word.substring(1);
                result.append(firstChar).append(restOfWord).append(" ");
            }
        }
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }
}
