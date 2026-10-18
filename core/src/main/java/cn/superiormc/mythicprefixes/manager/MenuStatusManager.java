package cn.superiormc.mythicprefixes.manager;

import cn.superiormc.mythicprefixes.gui.AbstractGUI;
import cn.superiormc.mythicprefixes.gui.GUIStatus;
import cn.superiormc.mythicprefixes.utils.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MenuStatusManager extends AbstractManager {

    public static MenuStatusManager menuStatusManager;

    private final Map<UUID, GUIStatus> openGuis = new ConcurrentHashMap<>();

    public MenuStatusManager() {
        menuStatusManager = this;
    }

    public GUIStatus getGUIStatus(Player player) {
        if (player == null) {
            return null;
        }
        return openGuis.get(player.getUniqueId());
    }

    public Object getOpeningGUI(Player player) {
        GUIStatus status = getGUIStatus(player);
        if (status == null) {
            return null;
        }
        return status.getGUI();
    }

    public boolean hasOpeningGUI(Player player) {
        return getGUIStatus(player) != null;
    }

    public void setGUIStatus(Player player, GUIStatus status) {
        if (player == null) {
            return;
        }
        if (status == null) {
            openGuis.remove(player.getUniqueId());
            return;
        }
        openGuis.put(player.getUniqueId(), status);
    }

    public void removeGUIStatus(Player player) {
        if (player != null) {
            openGuis.remove(player.getUniqueId());
        }
    }

    public boolean canOpenGUI(Player player, Object gui, boolean reopen) {
        if (player == null || gui == null) {
            return false;
        }
        GUIStatus current = getGUIStatus(player);
        long reopenCooldown = ConfigManager.configManager.getLong("choose-prefix-gui.cooldown.reopen", -1L);
        if (!reopen && current != null && current.getStatus() == GUIStatus.ALREADY_IN_COOLDOWN
                && reopenCooldown > 0L) {
            return false;
        }
        setGUIStatus(player, GUIStatus.of(gui,
                reopen ? GUIStatus.ACTION_OPEN_MENU : GUIStatus.CAN_REOPEN));
        return true;
    }

    public void removeOpenGUIStatus(Player player, Object gui) {
        if (player == null || gui == null) {
            return;
        }
        GUIStatus current = getGUIStatus(player);
        if (current == null || current.getGUI() != gui) {
            return;
        }
        long reopenCooldown = ConfigManager.configManager.getLong("choose-prefix-gui.cooldown.reopen", -1L);
        if (reopenCooldown <= 0L) {
            removeGUIStatus(player);
            return;
        }
        GUIStatus closing = GUIStatus.of(gui, GUIStatus.ALREADY_IN_COOLDOWN);
        setGUIStatus(player, closing);
        SchedulerUtil.runTaskLater(() -> openGuis.remove(player.getUniqueId(), closing), reopenCooldown);
    }

    public void clear(Player player) {
        if (player != null) {
            openGuis.remove(player.getUniqueId());
        }
    }

    public void closeAndClear(Player player) {
        Object opening = getOpeningGUI(player);
        if (opening instanceof AbstractGUI abstractGUI) {
            abstractGUI.closeGUI();
        }
        clear(player);
    }

    public void closeAndClearAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            closeAndClear(player);
        }
        openGuis.clear();
    }

    @Override
    public void onPluginReload() {
        closeAndClearAll();
    }

    @Override
    public void onPluginDisable() {
        closeAndClearAll();
    }
}
