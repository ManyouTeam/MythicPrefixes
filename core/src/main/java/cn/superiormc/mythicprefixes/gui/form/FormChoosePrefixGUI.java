package cn.superiormc.mythicprefixes.gui.form;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.gui.Filter;
import cn.superiormc.mythicprefixes.gui.FormGUI;
import cn.superiormc.mythicprefixes.gui.inv.ChoosePrefixGUI;
import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
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

    private final String selectedGroup;

    public FormChoosePrefixGUI(Player owner) {
        this(owner, "all");
    }

    public FormChoosePrefixGUI(Player owner, String group) {
        super(owner);
        selectedGroup = group == null || group.isEmpty() ? "all" : group;
        constructGUI();
    }

    @Override
    protected void constructGUI() {
        SimpleForm.Builder tempVal2 = SimpleForm.builder();
        buttonCache.clear();
        filterButtonCache = null;
        if (CacheManager.cacheManager.getPlayerCache(player) == null) {
            return;
        }
        for (ObjectPrefix prefix : ChoosePrefixGUI.collectVisiblePrefixes(player, selectedGroup, filter)) {
            ButtonComponent component = prefix.parseToBedrockButton(CacheManager.cacheManager.getPlayerCache(player));
            if (component != null) {
                tempVal2.button(component);
                buttonCache.put(component, prefix);
            }
        }
        for (AbstractButton button : ConfigManager.configManager.getButtons().values()) {
            if (button == null) {
                continue;
            }
            ButtonComponent component = button.parseToBedrockButton(CacheManager.cacheManager.getPlayerCache(player));
            if (component != null) {
                tempVal2.button(component);
                buttonCache.put(component, button);
            }
        }
        tempVal2.title(TextUtil.parse(ConfigManager.configManager.getString(player, "choose-prefix-gui.bedrock-title", "Tag GUI")));
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
                    tempVal1 = ButtonComponent.of(TextUtil.parse(filterSection.getString("bedrock-name") + "\n" + filterPlaceholder, player), FormImage.Type.URL, icon.split(";;")[1]);
                } else if (type.equals("path")) {
                    tempVal1 = ButtonComponent.of(TextUtil.parse(filterSection.getString("bedrock-name") + "\n" + filterPlaceholder, player), FormImage.Type.PATH, icon.split(";;")[1]);
                }
            } else {
                tempVal1 = ButtonComponent.of(TextUtil.parse(filterSection.getString("bedrock-name") + "\n" + filterPlaceholder, player));
            }
            if (tempVal1 != null) {
                tempVal2.button(tempVal1);
            }
            filterButtonCache = tempVal1;
        }
        tempVal2.validResultHandler((submittedForm, response) -> handleResponse(submittedForm, () -> {
            ButtonComponent clicked = response.clickedButton();
            if (filterButtonCache != null && filterButtonCache.equals(clicked)) {
                if (!MythicPrefixes.freeVersion) {
                    filter = filter.next();
                    updateGUI();
                }
                return;
            }
            AbstractButton button = buttonCache.get(clicked);
            if (button != null) {
                if (button instanceof ObjectPrefix && ((ObjectPrefix) button).isDynamicPrefix()) {
                    new FormDynamicPrefixGUI(player, (ObjectPrefix) button).openGUI();
                    return;
                }
                button.clickEvent(ClickType.LEFT, player);
                finishGUI();
            }
        }));
        tempVal2.closedOrInvalidResultHandler((submittedForm, response) ->
                handleResponse(submittedForm, this::finishGUI));
        form = tempVal2.build();
    }
}
