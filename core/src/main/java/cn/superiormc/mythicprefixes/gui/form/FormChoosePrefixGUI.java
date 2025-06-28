package cn.superiormc.mythicprefixes.gui.form;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.gui.Filter;
import cn.superiormc.mythicprefixes.gui.FormGUI;
import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.objects.ObjectCache;
import cn.superiormc.mythicprefixes.objects.PrefixStatus;
import cn.superiormc.mythicprefixes.objects.buttons.AbstractButton;
import cn.superiormc.mythicprefixes.objects.buttons.ObjectPrefix;
import cn.superiormc.mythicprefixes.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;

import java.util.HashMap;
import java.util.Map;


public class FormChoosePrefixGUI extends FormGUI {

    private final Map<ButtonComponent, AbstractButton> buttonCache = new HashMap<>();

    private ButtonComponent filterButtonCache;

    private Filter filter = Filter.ALL;

    public FormChoosePrefixGUI(Player owner) {
        super(owner);
        constructGUI();
    }

    @Override
    protected void constructGUI() {
        SimpleForm.Builder tempVal2 = SimpleForm.builder();
        ObjectCache cache = CacheManager.cacheManager.getPlayerCache(player);
        if (buttonCache.isEmpty()) {
            for (ObjectPrefix prefix : ConfigManager.configManager.getPrefixesWithoutHide()) {
                PrefixStatus status = prefix.getPrefixStatus(cache);
                if ((filter == Filter.ALL || (filter == Filter.USING && status == PrefixStatus.USING) || (
                        filter == Filter.CAN_USE && status == PrefixStatus.CAN_USE))
                        && prefix.shouldHideInGUI(player)) {
                    ButtonComponent tempVal1 = prefix.parseToBedrockButton(cache);
                    if (tempVal1 != null) {
                        tempVal2.button(tempVal1);
                    }
                    buttonCache.put(tempVal1, prefix);
                }
            }
            for (int slot : ConfigManager.configManager.getButtons().keySet()) {
                AbstractButton button = ConfigManager.configManager.getButtons().get(slot);
                if (button == null) {
                    continue;
                }
                ButtonComponent tempVal1 = button.parseToBedrockButton(cache);
                if (tempVal1 != null) {
                    tempVal2.button(tempVal1);
                }
                buttonCache.put(tempVal1, button);
            }
        }
        tempVal2.title(TextUtil.parse(ConfigManager.configManager.getString("choose-prefix-gui." + "title", "Tag GUI")));
        ConfigurationSection filterSection = ConfigManager.configManager.getConfigurationSection("choose-prefix-gui.filter-item");
        if (filterSection != null && !MythicPrefixes.freeVersion) {
            String filterPlaceholder = filterSection.getString("placeholder.all");
            if (filter == Filter.USING) {
                filterPlaceholder = filterSection.getString("placeholder.using");
            } else if (filter == Filter.CAN_USE) {
                filterPlaceholder = filterSection.getString("placeholder.can-use");
            }
            ButtonComponent tempVal1 = null;
            String icon = filterSection.getString("bedrock.icon", filterSection.getString("bedrock-icon"));
            if (icon != null && icon.split(";;").length == 2) {
                String type = icon.split(";;")[0].toLowerCase();
                if (type.equals("url")) {
                    tempVal1 = ButtonComponent.of(TextUtil.parse(filterSection.getString("name") + "\n" + filterPlaceholder), FormImage.Type.URL, icon.split(";;")[1]);
                } else if (type.equals("path")) {
                    tempVal1 = ButtonComponent.of(TextUtil.parse(filterSection.getString("name") + "\n" + filterPlaceholder), FormImage.Type.PATH, icon.split(";;")[1]);
                }
            } else {
                tempVal1 = ButtonComponent.of(TextUtil.parse(filterSection.getString("name") + "\n" + filterPlaceholder));
            }
            if (tempVal1 != null) {
                tempVal2.button(tempVal1);
            }
            filterButtonCache = tempVal1;
        }
        tempVal2.validResultHandler(response -> {
            if (filterButtonCache != null && response.clickedButton().equals(filterButtonCache)) {
                if (!MythicPrefixes.freeVersion) {
                    if (filter == Filter.ALL) {
                        filter = Filter.USING;
                    } else if (filter == Filter.USING) {
                        filter = Filter.CAN_USE;
                    } else if (filter == Filter.CAN_USE) {
                        filter = Filter.ALL;
                    }
                    buttonCache.clear();
                    constructGUI();
                    openGUI();
                }
            } else {
                buttonCache.get(response.clickedButton()).clickEvent(ClickType.LEFT, player);
            }
        });
        form = tempVal2.build();
    }
}
