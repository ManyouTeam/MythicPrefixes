package cn.superiormc.mythicprefixes.database;

import cc.carm.lib.easysql.EasySQL;
import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.action.query.QueryAction;
import cc.carm.lib.easysql.hikari.HikariConfig;
import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.objects.ObjectCache;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.utils.TextUtil;
import org.bukkit.Bukkit;

import java.sql.SQLException;
import java.util.Objects;

public class SQLDatabase {
    public static SQLManager sqlManager;

    public static void initSQL() {
        Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §fTrying connect to SQL database...");
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(ConfigManager.configManager.getString("database.jdbc-class"));
        config.setJdbcUrl(ConfigManager.configManager.getString("database.jdbc-url"));
        if (ConfigManager.configManager.getString("database.properties.user") != null) {
            config.setUsername(ConfigManager.configManager.getString("database.properties.user"));
            config.setPassword(ConfigManager.configManager.getString("database.properties.password"));
        }
        sqlManager = EasySQL.createManager(config);
        try {
            if (!sqlManager.getConnection().isValid(5)) {
                Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §cFailed connect to SQL database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        createTable();
    }

    public static void closeSQL() {
        if (Objects.nonNull(sqlManager)) {
            EasySQL.shutdownManager(sqlManager);
            sqlManager = null;
        }
    }

    public static void createTable() {
        sqlManager.createTable("mythicprefixes")
                .addColumn("playerUUID", "VARCHAR(36) NOT NULL PRIMARY KEY")
                .addColumn("prefixID", "TEXT")
                .build().execute(null);
    }

    public static void checkData(ObjectCache cache) {
        QueryAction queryAction = sqlManager.createQuery()
                .inTable("mythicprefixes")
                .selectColumns("playerUUID",
                        "prefixID")
                .addCondition("playerUUID = '" + cache.getPlayer().getUniqueId().toString() + "'")
                .build();
        queryAction.executeAsync((result) -> {
            while (result.getResultSet().next()) {
                cache.setActivePrefixes(result.getResultSet().getString("prefixID"));
            }
        });
    }

    public static void updateData(ObjectCache cache, boolean quitServer) {
        String playerUUID = cache.getPlayer().getUniqueId().toString();
        sqlManager.createReplace("mythicprefixes")
                .setColumnNames("playerUUID",
                        "prefixID")
                .setParams(playerUUID, cache.getActivePrefixesID())
                .executeAsync();
        if (quitServer) {
            CacheManager.cacheManager.removePlayerCache(cache.getPlayer());
        }
    }

    public static void updateDataNoAsync(ObjectCache cache) {
        String playerUUID = cache.getPlayer().getUniqueId().toString();
        try {
            sqlManager.createReplace("mythicprefixes")
                    .setColumnNames("playerUUID",
                            "prefixID")
                    .setParams(playerUUID, cache.getActivePrefixesID())
                    .execute();
            CacheManager.cacheManager.removePlayerCache(cache.getPlayer());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
