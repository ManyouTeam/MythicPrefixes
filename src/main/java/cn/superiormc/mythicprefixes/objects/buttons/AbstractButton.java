package cn.superiormc.mythicprefixes.objects.buttons;

import cn.superiormc.mythicprefixes.api.MythicPrefixesAPI;
import cn.superiormc.mythicprefixes.manager.ErrorManager;
import cn.superiormc.mythicprefixes.objects.ObjectAction;
import cn.superiormc.mythicprefixes.objects.ObjectCache;
import cn.superiormc.mythicprefixes.objects.ObjectCondition;
import cn.superiormc.mythicprefixes.utils.CommonUtil;
import cn.superiormc.mythicprefixes.utils.ItemUtil;
import cn.superiormc.mythicprefixes.utils.TextUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.util.FormImage;

public abstract class AbstractButton {

    protected ConfigurationSection config;

    protected ObjectAction action;

    protected ObjectCondition condition;

    protected ButtonType type;

    public AbstractButton(ConfigurationSection config) {
        if (config == null) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicPrefixes] §cError: " +
                    "Can not found config for button, there is something wrong in your menu configs!");
            return;
        }
        this.config = config;
    }

    public AbstractButton(){
        // Empty...
    }

    public ItemStack getDisplayItem(Player player) {
        return new ItemStack(Material.STONE);
    }

    public void clickEvent(ClickType type, Player player) {
        return;
    }

    public ButtonType getType() {
        return type;
    }

    public ObjectAction getAction() {
        return action;
    }

    public ObjectCondition getCondition() {
        return condition;
    }

    public ButtonComponent parseToBedrockButton(ObjectCache cache) {
        Player player = cache.getPlayer();
        String icon = config.getString("bedrock.icon", config.getString("bedrock-icon"));
        if (config.getBoolean("bedrock.hide", false)) {
            return null;
        }
        String tempVal3 = TextUtil.parse(ItemUtil.getItemNameWithoutVanilla(getDisplayItem(player)), player);
        String tempVal4 = config.getString("bedrock.extra-line");
        if (tempVal4 != null && !tempVal4.isEmpty()) {
            if (this instanceof ObjectPrefix) {
                tempVal3 = CommonUtil.modifyString(tempVal3 + "\n" + TextUtil.parse(tempVal4, player), "status",
                        MythicPrefixesAPI.getStatusPlaceholder((ObjectPrefix) this, cache));
            } else {
                tempVal3 = tempVal3 + "\n" + TextUtil.parse(tempVal4, player);
            }
        }
        ButtonComponent tempVal1 = null;
        if (icon != null && icon.split(";;").length == 2) {
            String type = icon.split(";;")[0].toLowerCase();
            if (type.equals("url")) {
                tempVal1 = ButtonComponent.of(tempVal3, FormImage.Type.URL, icon.split(";;")[1]);
            } else if (type.equals("path")) {
                tempVal1 = ButtonComponent.of(tempVal3, FormImage.Type.PATH, icon.split(";;")[1]);
            }
        } else {
            tempVal1 = ButtonComponent.of(tempVal3);
        }
        return tempVal1;
    }

}
