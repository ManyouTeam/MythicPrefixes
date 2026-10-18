package cn.superiormc.mythicprefixes.gui;

import cn.superiormc.mythicprefixes.manager.MenuStatusManager;
import cn.superiormc.mythicprefixes.utils.SchedulerUtil;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.Form;
import org.geysermc.floodgate.api.FloodgateApi;

public abstract class FormGUI extends AbstractGUI {

    protected Form form;

    private Form consumedForm;

    public FormGUI(Player owner) {
        super(owner);
    }

    @Override
    public void updateGUI() {
        form = null;
        constructGUI();
        if (form != null) {
            FloodgateApi.getInstance().sendForm(player.getUniqueId(), form);
        }
    }

    @Override
    public void openGUI(boolean reopen) {
        GUIStatus previousStatus = MenuStatusManager.menuStatusManager.getGUIStatus(player);
        if (!MenuStatusManager.menuStatusManager.canOpenGUI(player, this, reopen)) {
            return;
        }
        if (form == null || form == consumedForm) {
            form = null;
            constructGUI();
        }
        if (form != null) {
            FloodgateApi.getInstance().sendForm(player.getUniqueId(), form);
        } else {
            MenuStatusManager.menuStatusManager.setGUIStatus(player, previousStatus);
        }
    }

    @Override
    public void closeGUI() {
        finishGUI();
    }

    public Form getForm() {
        return form;
    }

    protected void handleResponse(Form submittedForm, Runnable action) {
        SchedulerUtil.runSync(player, () -> {
            if (!player.isOnline() || submittedForm == null || submittedForm != form
                    || submittedForm == consumedForm
                    || MenuStatusManager.menuStatusManager.getOpeningGUI(player) != this) {
                return;
            }
            consumedForm = submittedForm;
            action.run();
        });
    }
}
