package cn.superiormc.mythicprefixes.gui.form;

import cn.superiormc.mythicprefixes.gui.FormGUI;
import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.manager.LanguageManager;
import cn.superiormc.mythicprefixes.methods.DynamicPrefixes;
import cn.superiormc.mythicprefixes.objects.ObjectCache;
import cn.superiormc.mythicprefixes.objects.PrefixStatus;
import cn.superiormc.mythicprefixes.objects.buttons.ObjectPrefix;
import cn.superiormc.mythicprefixes.utils.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.geysermc.cumulus.form.CustomForm;

import java.util.ArrayList;
import java.util.List;

public class FormDynamicPrefixGUI extends FormGUI {

    private final ObjectPrefix prefix;

    public FormDynamicPrefixGUI(Player owner, ObjectPrefix prefix) {
        super(owner);
        this.prefix = prefix;
        constructGUI();
    }

    @Override
    protected void constructGUI() {
        ObjectCache cache = CacheManager.cacheManager.getPlayerCache(player);
        String current = cache.getApprovedDynamicPrefixValue(prefix.getId());
        String pending = cache.getPendingDynamicPrefixValue(prefix.getId());
        String empty = LanguageManager.languageManager.getStringText(player, "dynamic-prefix.none");
        PrefixStatus status = prefix.getPrefixStatus(cache);
        List<String> actions = new ArrayList<>();
        actions.add(TextUtil.parse(LanguageManager.languageManager.getStringText(player,
                "dynamic-prefix.form-action-submit")));
        if (status == PrefixStatus.CAN_USE) {
            actions.add(TextUtil.parse(LanguageManager.languageManager.getStringText(player,
                    "dynamic-prefix.form-action-equip")));
        } else if (status == PrefixStatus.USING) {
            actions.add(TextUtil.parse(LanguageManager.languageManager.getStringText(player,
                    "dynamic-prefix.form-action-unequip")));
        }
        CustomForm.Builder builder = CustomForm.builder()
                .title(TextUtil.parse(LanguageManager.languageManager.getStringText(player,
                        "dynamic-prefix.form-title", "", "prefix", prefix.getId())))
                .label(TextUtil.parse(LanguageManager.languageManager.getStringText(player,
                        "dynamic-prefix.form-label", "",
                        "current", current == null || current.isEmpty() ? empty : current,
                        "pending", pending == null || pending.isEmpty() ? empty : pending)))
                .dropdown(TextUtil.parse(LanguageManager.languageManager.getStringText(player,
                        "dynamic-prefix.form-action")), actions)
                .input(TextUtil.parse(LanguageManager.languageManager.getStringText(player,
                        "dynamic-prefix.form-input")), "", current == null ? "" : current);
        builder.validResultHandler(response -> {
            if (response.asDropdown(1) == 1) {
                prefix.clickEvent(ClickType.LEFT, player);
                return;
            }
            DynamicPrefixes.submitDynamicPrefix(player, prefix, response.asInput(2));
        });
        form = builder.build();
    }
}
