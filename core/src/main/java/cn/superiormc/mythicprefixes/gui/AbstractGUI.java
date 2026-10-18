package cn.superiormc.mythicprefixes.gui;

import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.manager.MenuStatusManager;
import cn.superiormc.mythicprefixes.utils.SchedulerUtil;
import org.bukkit.entity.Player;

public abstract class AbstractGUI {

    private boolean inClickCooldown;

    protected final Player player;

    public AbstractGUI(Player player) {
        this.player = player;
    }

    protected abstract void constructGUI();

    public void updateGUI() {
        constructGUI();
    }

    public void openGUI() {
        openGUI(false);
    }

    public abstract void openGUI(boolean reopen);

    public abstract void closeGUI();

    public void finishGUI() {
        if (MenuStatusManager.menuStatusManager != null) {
            MenuStatusManager.menuStatusManager.removeOpenGUIStatus(player, this);
        }
    }

    public void addCooldown() {
        long ticks = ConfigManager.configManager.getLong("choose-prefix-gui.cooldown.click", -1L);
        if (!inClickCooldown && ticks > 0L) {
            inClickCooldown = true;
            SchedulerUtil.runTaskLater(() -> inClickCooldown = false, ticks);
        }
    }

    public boolean getCooldown() {
        return inClickCooldown;
    }

    public Player getPlayer() {
        return player;
    }
}
