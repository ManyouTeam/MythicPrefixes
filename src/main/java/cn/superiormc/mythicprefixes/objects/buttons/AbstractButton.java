package cn.superiormc.mythicprefixes.objects.buttons;

import cn.superiormc.mythicprefixes.manager.ErrorManager;
import cn.superiormc.mythicprefixes.objects.ObjectAction;
import cn.superiormc.mythicprefixes.objects.ObjectCondition;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

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

}
