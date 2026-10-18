package cn.superiormc.mythicprefixes.gui;

public enum Filter {

    CAN_USE,
    USING,
    ALL;

    public Filter next() {
        if (this == ALL) {
            return USING;
        }
        if (this == USING) {
            return CAN_USE;
        }
        return ALL;
    }
}
