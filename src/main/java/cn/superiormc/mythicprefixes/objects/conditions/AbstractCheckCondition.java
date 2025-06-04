package cn.superiormc.mythicprefixes.objects.conditions;

import cn.superiormc.mythicprefixes.manager.ErrorManager;
import cn.superiormc.mythicprefixes.utils.TextUtil;
import org.bukkit.entity.Player;

public abstract class AbstractCheckCondition {

    private final String type;

    private String[] requiredArgs;

    public AbstractCheckCondition(String type) {
        this.type = type;
    }

    protected void setRequiredArgs(String... requiredArgs) {
        this.requiredArgs = requiredArgs;
    }

    public boolean checkCondition(ObjectSingleCondition singleCondition, Player player) {
        if (requiredArgs != null) {
            for (String arg : requiredArgs) {
                if (!singleCondition.getSection().contains(arg)) {
                    ErrorManager.errorManager.sendErrorMessage("§cError: Your condition missing required arg: " + arg + ".");
                    return true;
                }
            }
        }
        return onCheckCondition(singleCondition, player);
    }

    protected abstract boolean onCheckCondition(ObjectSingleCondition singleCondition, Player player);

    public String getType() {
        return type;
    }
}
