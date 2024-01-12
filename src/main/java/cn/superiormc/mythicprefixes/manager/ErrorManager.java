package cn.superiormc.mythicprefixes.manager;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class ErrorManager {

    public static ErrorManager errorManager;

    public boolean getError = false;

    private String lastErrorMessage = "";

    public ErrorManager(){
        errorManager = this;
    }

    public void sendErrorMessage(String message){
        if (!getError || !message.equals(lastErrorMessage)) {
            Bukkit.getConsoleSender().sendMessage(message);
            lastErrorMessage = message;
            getError = true;
            new BukkitRunnable() {
                @Override
                public void run() {
                    getError = false;
                }
            }.runTaskLater(MythicPrefixes.instance, 100);
        }
    }
}
