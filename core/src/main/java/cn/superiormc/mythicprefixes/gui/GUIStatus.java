package cn.superiormc.mythicprefixes.gui;

public class GUIStatus {

    public static final int CAN_REOPEN = 0;

    public static final int ACTION_OPEN_MENU = 1;

    public static final int ALREADY_IN_COOLDOWN = 2;

    private final int status;

    private final Object gui;

    private GUIStatus(Object gui, int status) {
        this.gui = gui;
        this.status = status;
    }

    public Object getGUI() {
        return gui;
    }

    public int getStatus() {
        return status;
    }

    public static GUIStatus of(Object gui, int status) {
        return new GUIStatus(gui, status);
    }
}
