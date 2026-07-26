package cn.superiormc.mythicprefixes.objects;

import cn.superiormc.mythicprefixes.manager.LanguageManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class AbstractCommand {

    protected String id;

    protected String requiredPermission;

    protected boolean onlyInGame;

    protected Integer[] requiredArgLength;

    public AbstractCommand() {
        // EMPTY
    }

    public abstract void executeCommandInGame(String[] args, Player player);

    public void executeCommandInConsole(String[] args) {
        LanguageManager.languageManager.sendStringText("error.in-game");
    }

    public String getId() {
        return id;
    }

    public boolean getOnlyInGame() {
        return onlyInGame;
    }

    public String getRequiredPermission() {
        return requiredPermission;
    }

    public boolean getLengthCorrect(int length) {
        for (int number : requiredArgLength) {
            if (number == length) {
                return true;
            }
        }
        return false;
    }

    public List<String> getTabResult(int length) {
        return new ArrayList<>();
    }

    protected List<String> getTabResult(String[] args) {
        return getTabResult(args.length);
    }

    public List<String> filterTabResult(String[] args) {
        List<String> results = getTabResult(args);
        if (args.length == 0) {
            return results;
        }

        String currentInput = args[args.length - 1].toLowerCase(Locale.ROOT);
        results.removeIf(result -> !result.toLowerCase(Locale.ROOT).startsWith(currentInput));
        return results;
    }
}
