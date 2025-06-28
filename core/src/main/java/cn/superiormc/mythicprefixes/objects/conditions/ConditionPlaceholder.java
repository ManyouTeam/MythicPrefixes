package cn.superiormc.mythicprefixes.objects.conditions;

import cn.superiormc.mythicprefixes.manager.ErrorManager;
import org.bukkit.entity.Player;

public class ConditionPlaceholder extends AbstractCheckCondition {

    public ConditionPlaceholder() {
        super("placeholder");
        setRequiredArgs("placeholder", "rule", "value");
    }

    @Override
    protected boolean onCheckCondition(ObjectSingleCondition singleCondition, Player player) {
        String placeholder = singleCondition.getString("placeholder", player);
        String value = singleCondition.getString("value", player);
        switch (singleCondition.getString("rule")) {
            case ">=":
                return Double.parseDouble(placeholder) >= Double.parseDouble(value);
            case ">":
                return Double.parseDouble(placeholder) > Double.parseDouble(value);
            case "=":
                return Double.parseDouble(placeholder) == Double.parseDouble(value);
            case "<":
                return Double.parseDouble(placeholder) < Double.parseDouble(value);
            case "<=":
                return Double.parseDouble(placeholder) <= Double.parseDouble(value);
            case "==":
                return placeholder.equals(value);
            case "!=":
                return !placeholder.equals(value);
            case "*=":
                return placeholder.contains(value);
            case "=*":
                return value.contains(placeholder);
            case "!*=":
                return !placeholder.contains(value);
            case "!=*":
                return !value.contains(placeholder);
            default:
                ErrorManager.errorManager.sendErrorMessage("§cError: Your placeholder condition can not being correctly load.");
                return true;
        }
    }
}
