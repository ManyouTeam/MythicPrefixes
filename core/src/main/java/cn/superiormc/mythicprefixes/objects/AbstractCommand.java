package cn.superiormc.mythicprefixes.objects;

import cn.superiormc.mythicprefixes.manager.LanguageManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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
}
