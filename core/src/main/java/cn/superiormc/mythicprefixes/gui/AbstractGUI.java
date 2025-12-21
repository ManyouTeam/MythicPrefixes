package cn.superiormc.mythicprefixes.gui;

import org.bukkit.entity.Player;

public abstract class AbstractGUI {

    protected Player player;

    public AbstractGUI(Player player) {
        this.player = player;
    }

    protected abstract void constructGUI();

    public abstract void openGUI();

    public Player getPlayer() {
        return player;
    }
}