package cn.superiormc.mythicprefixes.utils;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.manager.ErrorManager;
import com.google.common.base.Enums;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemUtil {

    public static ItemStack buildItemStack(@NotNull Player player, @Nullable ConfigurationSection section, String... args) {
        ItemStack item = new ItemStack(Material.STONE);
        if (section == null) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicPrefixes] §cError: Can not parse item because the " +
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
        String displayNameKey = section.getString("name");
        if (displayNameKey != null) {
            if (MythicPrefixes.isPaper && ConfigManager.configManager.getBoolean("use-component.item")) {
                meta.displayName(MiniMessage.miniMessage().deserialize(TextUtil.withPAPI(
                        CommonUtil.modifyString(displayNameKey, args), player)));
            } else {
                meta.setDisplayName(TextUtil.parse(CommonUtil.modifyString(displayNameKey, args), player));
            }
        }
        List<String> loreKey = section.getStringList("lore");
        if (!loreKey.isEmpty()) {
            List<String> newLore = new ArrayList<>();
            List<Component> veryNewLore = new ArrayList<>();
            for (String singleLore : section.getStringList("lore")) {
                if (singleLore.isEmpty()) {
                    if (MythicPrefixes.isPaper && ConfigManager.configManager.getBoolean("use-component.item")) {
                        veryNewLore.add(Component.space());
                    } else {
                        newLore.add(" ");
                    }
                    continue;
                }
                if (MythicPrefixes.isPaper && ConfigManager.configManager.getBoolean("use-component.item")) {
                    veryNewLore.add(MiniMessage.miniMessage().deserialize(TextUtil.withPAPI(singleLore, player)));
                } else {
                    newLore.add(TextUtil.parse(singleLore, player));
                }
            }
            if (!newLore.isEmpty()) {
                meta.setLore(newLore);
            }
            if (!veryNewLore.isEmpty()) {
                meta.lore(veryNewLore);
            }
        }
        if (CommonUtil.getMajorVersion() >= 14) {
            int customModelDataKey = section.getInt("custom-model-data", section.getInt("cmd", -1));
            if (customModelDataKey > 0) {
                meta.setCustomModelData(customModelDataKey);
            }
        }
        List<String> itemFlagKey = section.getStringList("flags");
        if (!itemFlagKey.isEmpty()) {
            for (String flag : itemFlagKey) {
                flag = flag.toUpperCase();
                ItemFlag itemFlag = Enums.getIfPresent(ItemFlag.class, flag).orNull();
                if (itemFlag != null) {
                    meta.addItemFlags(itemFlag);
                }
            }
        }
        ConfigurationSection enchantsKey = section.getConfigurationSection("enchants");
        if (enchantsKey != null) {
            for (String ench : enchantsKey.getKeys(false)) {
                Enchantment vanillaEnchant = Enchantment.getByKey(NamespacedKey.minecraft(ench.toLowerCase()));
                if (vanillaEnchant != null) {
                    meta.addEnchant(vanillaEnchant, enchantsKey.getInt(ench), true);
                }
            }
        }
        if (meta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) meta;
            String skullTextureNameKey = section.getString("skull-meta", section.getString("skull"));
            if (skullTextureNameKey != null) {
                GameProfile profile = new GameProfile(UUID.randomUUID(), "");
                profile.getProperties().put("textures", new Property("textures", skullTextureNameKey));
                try {
                    Method mtd = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
                    mtd.setAccessible(true);
                    mtd.invoke(skullMeta, profile);
                } catch (Exception exception) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicPrefixes] §cError: Can not parse skull texture in a item!");
                }
            }
            item.setItemMeta(skullMeta);
        }
        item.setItemMeta(meta);
        return item;
    }

}
