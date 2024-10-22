package cn.superiormc.mythicprefixes.objects.buttons;

import cn.superiormc.mythicprefixes.manager.ErrorManager;
import cn.superiormc.mythicprefixes.objects.ObjectAction;
import cn.superiormc.mythicprefixes.objects.ObjectCondition;
import cn.superiormc.mythicprefixes.utils.ItemUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ObjectButton extends AbstractButton {


    public ObjectButton(ConfigurationSection config) {
        super(config);
        this.type = ButtonType.COMMON;
        initButton();
    }

    private void initButton() {
        this.action = new ObjectAction(config.getConfigurationSection("actions"));
        this.condition = new ObjectCondition(config.getConfigurationSection("conditions"));

    }

    @Override
    public void clickEvent(ClickType type, Player player) {
        if (condition != null && !condition.getAllBoolean(player)) {
            return;
        }
        if (action != null) {
            action.runAllActions(player);
        }
    }

    @Override
    public ItemStack getDisplayItem(Player player) {
        return ItemUtil.buildItemStack(player, config);
    }
}
