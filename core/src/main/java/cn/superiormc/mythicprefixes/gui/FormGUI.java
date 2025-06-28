package cn.superiormc.mythicprefixes.gui;

import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.Form;
import org.geysermc.floodgate.api.FloodgateApi;

public abstract class FormGUI extends AbstractGUI {

    protected Form form;

    public FormGUI(Player owner) {
        super(owner);
    }

    public void openGUI() {
        if (form != null) {
            FloodgateApi.getInstance().sendForm(player.getUniqueId(), form);
        }
    }

    public Form getForm() {
        return form;
    }
}
