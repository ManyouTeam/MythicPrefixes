package cn.superiormc.mythicprefixes.objects.actions;


import cn.superiormc.mythicprefixes.manager.ErrorManager;
import org.bukkit.entity.Player;

public abstract class AbstractRunAction {

    private final String type;

    private String[] requiredArgs;

    public AbstractRunAction(String type) {
        this.type = type;
    }

    protected void setRequiredArgs(String... requiredArgs) {
        this.requiredArgs = requiredArgs;
    }

    public void runAction(ObjectSingleAction singleAction, Player player) {
        for (String arg : requiredArgs) {
            if (!singleAction.getSection().contains(arg)) {
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicPrefixes] §cError: Your action missing required arg: " + arg + ".");
                return;
            }
        }
        onDoAction(singleAction, player);
    }

    protected abstract void onDoAction(ObjectSingleAction singleAction, Player player);

    public String getType() {
        return type;
    }
}
