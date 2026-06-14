package cn.superiormc.mythicprefixes.manager;

import cn.superiormc.mythicprefixes.database.AbstractDatabase;
import cn.superiormc.mythicprefixes.database.SQLDatabase;
import cn.superiormc.mythicprefixes.database.YamlDatabase;

public class DatabaseManager {

    public static DatabaseManager databaseManager;

    public AbstractDatabase database;

    public DatabaseManager() {
        databaseManager = this;
        if (ConfigManager.configManager.getBoolean("database.enabled")) {
            database = new SQLDatabase();
        } else {
            database = new YamlDatabase();
        }
        database.onInit();
    }
}

