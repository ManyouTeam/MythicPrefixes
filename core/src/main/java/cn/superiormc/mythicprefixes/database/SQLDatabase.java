package cn.superiormc.mythicprefixes.database;

import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.objects.ObjectCache;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.utils.TextUtil;

import java.sql.SQLException;

import cn.superiormc.mythicprefixes.database.sql.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SQLDatabase extends AbstractDatabase {

    private HikariDataSource dataSource;
    private DatabaseDialect dialect;

    /* =========================
       Lifecycle
     ========================= */

    @Override
    public void onInit() {
        onClose();

        TextUtil.sendMessage(null,
                TextUtil.pluginPrefix() + " §fConnecting to SQL database...");

        String jdbcUrl = ConfigManager.configManager.getString("database.jdbc-url");

        initDialect(jdbcUrl);
        dialect.needExtraDownload(jdbcUrl);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);

        String user = ConfigManager.configManager.getString("database.properties.user");
        if (user != null && !user.isEmpty()) {
            config.setUsername(user);
            config.setPassword(
                    ConfigManager.configManager.getString("database.properties.password")
            );
        }

        config.setPoolName("MythicPrefixes-Hikari");
        config.setMaximumPoolSize(dialect.maxPoolSize());
        config.setMinimumIdle(dialect.minIdle());

        if (dialect.forceSingleConnection()) {
            config.setMaximumPoolSize(1);
            config.setMinimumIdle(1);
        }

        dataSource = new HikariDataSource(config);

        createTable();
    }

    @Override
    public void onClose() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    /* =========================
       Dialect
     ========================= */

    private void initDialect(String jdbcUrl) {
        List<DatabaseDialect> dialects = List.of(
                new MySQLDialect(),
                new PostgreSQLDialect(),
                new H2Dialect(),
                new SQLiteDialect()
        );

        this.dialect = dialects.stream()
                .filter(d -> d.matches(jdbcUrl))
                .findFirst()
                .orElse(new MySQLDialect());
    }

    private void createTable() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(dialect.createPrefixTable());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* =========================
       Load
     ========================= */

    @Override
    public void checkData(ObjectCache cache) {
        CompletableFuture.runAsync(
                () -> loadData(cache),
                DatabaseExecutor.EXECUTOR
        );
    }

    private void loadData(ObjectCache cache) {
        String playerUUID = cache.getPlayer().getUniqueId().toString();

        String sql = """
                SELECT prefixID
                FROM mythicprefixes
                WHERE playerUUID = ?
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, playerUUID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cache.setActivePrefixes(rs.getString("prefixID"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* =========================
       Save
     ========================= */

    @Override
    public void updateData(ObjectCache cache, boolean quitServer) {
        CompletableFuture.runAsync(() -> {
            saveData(cache);
            if (quitServer) {
                CacheManager.cacheManager.removePlayerCache(cache.getPlayer());
            }
        }, DatabaseExecutor.EXECUTOR);
    }

    @Override
    public void updateDataOnDisable(ObjectCache cache, boolean disable) {
        saveData(cache);
        CacheManager.cacheManager.removePlayerCache(cache.getPlayer());
        if (disable) {
            DatabaseExecutor.EXECUTOR.shutdownNow();
        }
    }

    private void saveData(ObjectCache cache) {
        String playerUUID = cache.getPlayer().getUniqueId().toString();
        String sql = dialect.upsertPrefix();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, playerUUID);
            ps.setString(2, cache.getActivePrefixesID());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
