package cn.superiormc.mythicprefixes.database;

import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.objects.DynamicPrefixRequest;
import cn.superiormc.mythicprefixes.objects.ObjectCache;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.utils.TextUtil;

import java.sql.SQLException;

import cn.superiormc.mythicprefixes.database.sql.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class SQLDatabase extends AbstractDatabase {

    private HikariDataSource dataSource;

    private DatabaseDialect dialect;

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
        if (dialect != null) {
            dialect.closeDrivers();
        }
        dataSource = null;
        dialect = null;
    }

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
            stmt.execute(dialect.createDynamicPrefixTable());
            stmt.execute(dialect.createDynamicPrefixRequestTable());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void checkData(ObjectCache cache) {
        CompletableFuture.runAsync(
                () -> loadData(cache),
                DatabaseExecutor.getExecutor()
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

            String activePrefixes = null;
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    activePrefixes = rs.getString("prefixID");
                }
            }

            try (PreparedStatement ps2 = conn.prepareStatement("""
                    SELECT prefixID, approvedValue
                    FROM mythicprefixes_dynamic
                    WHERE playerUUID = ?
                    """)) {
                ps2.setString(1, playerUUID);
                try (ResultSet rs2 = ps2.executeQuery()) {
                    while (rs2.next()) {
                        String prefixID = rs2.getString("prefixID");
                        cache.setDynamicPrefixValue(prefixID, rs2.getString("approvedValue"));
                    }
                }
            }
            try (PreparedStatement ps3 = conn.prepareStatement("""
                    SELECT prefixID, pendingValue
                    FROM mythicprefixes_dynamic_pending
                    WHERE playerUUID = ?
                    """)) {
                ps3.setString(1, playerUUID);
                try (ResultSet rs3 = ps3.executeQuery()) {
                    while (rs3.next()) {
                        cache.setPendingDynamicPrefixValue(rs3.getString("prefixID"), rs3.getString("pendingValue"));
                    }
                }
            }
            if (activePrefixes != null) {
                cache.setActivePrefixes(activePrefixes);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateData(ObjectCache cache, boolean quitServer) {
        CompletableFuture.runAsync(() -> {
            saveData(cache);
            if (quitServer) {
                CacheManager.cacheManager.removePlayerCache(cache);
            }
        }, DatabaseExecutor.getExecutor());
    }

    @Override
    public void updateDataOnDisable(ObjectCache cache, boolean disable) {
        saveData(cache);
        CacheManager.cacheManager.removePlayerCache(cache);
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

    @Override
    public CompletableFuture<Void> saveDynamicPrefixRequest(org.bukkit.entity.Player player, String prefixID, String value) {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(dialect.upsertDynamicPrefixRequest())) {
                ps.setString(1, player.getUniqueId().toString());
                ps.setString(2, player.getName());
                ps.setString(3, prefixID);
                ps.setString(4, value);
                ps.executeUpdate();
                try (PreparedStatement ps2 = conn.prepareStatement(dialect.upsertDynamicPrefix())) {
                    ObjectCache cache = CacheManager.cacheManager.getPlayerCache(player);
                    String markedValue = cache == null ? ObjectCache.markDynamicPrefixPending(value) : cache.getDynamicPrefixValue(prefixID);
                    ps2.setString(1, player.getUniqueId().toString());
                    ps2.setString(2, prefixID);
                    ps2.setString(3, markedValue == null ? ObjectCache.markDynamicPrefixPending(value) : markedValue);
                    ps2.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, DatabaseExecutor.getExecutor());
    }

    @Override
    public CompletableFuture<Collection<DynamicPrefixRequest>> getPendingDynamicPrefixRequests() {
        return CompletableFuture.supplyAsync(() -> {
            List<DynamicPrefixRequest> result = new ArrayList<>();
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement("""
                         SELECT playerUUID, playerName, prefixID, pendingValue
                         FROM mythicprefixes_dynamic_pending
                         WHERE pendingValue IS NOT NULL AND pendingValue <> ''
                         """);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new DynamicPrefixRequest(
                            rs.getString("playerUUID"),
                            rs.getString("playerName"),
                            rs.getString("prefixID"),
                            rs.getString("pendingValue")
                    ));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return result;
        }, DatabaseExecutor.getExecutor());
    }

    @Override
    public CompletableFuture<Integer> getPendingDynamicPrefixRequestAmount() {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement("""
                         SELECT COUNT(*) AS amount
                         FROM mythicprefixes_dynamic_pending
                         WHERE pendingValue IS NOT NULL AND pendingValue <> ''
                         """);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("amount");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        }, DatabaseExecutor.getExecutor());
    }

    @Override
    public CompletableFuture<Boolean> approveDynamicPrefixRequest(String playerUUID, String prefixID) {
        return handleDynamicPrefixRequest(playerUUID, prefixID, true);
    }

    @Override
    public CompletableFuture<Boolean> denyDynamicPrefixRequest(String playerUUID, String prefixID) {
        return handleDynamicPrefixRequest(playerUUID, prefixID, false);
    }

    private CompletableFuture<Boolean> handleDynamicPrefixRequest(String playerUUID, String prefixID, boolean approve) {
        return CompletableFuture.supplyAsync(() -> {
            Connection conn = null;
            try {
                conn = dataSource.getConnection();
                conn.setAutoCommit(false);
                boolean exists;
                try (PreparedStatement ps = conn.prepareStatement("""
                        SELECT pendingValue
                        FROM mythicprefixes_dynamic_pending
                        WHERE playerUUID = ? AND prefixID = ? AND pendingValue IS NOT NULL AND pendingValue <> ''
                        """)) {
                    ps.setString(1, playerUUID);
                    ps.setString(2, prefixID);
                    try (ResultSet rs = ps.executeQuery()) {
                        exists = rs.next();
                    }
                }
                if (!exists) {
                    conn.rollback();
                    return false;
                }
                if (approve) {
                    try (PreparedStatement ps = conn.prepareStatement(dialect.upsertDynamicPrefix())) {
                        ps.setString(1, playerUUID);
                        ps.setString(2, prefixID);
                        ps.setString(3, ObjectCache.markDynamicPrefixApproved(getPendingValue(conn, playerUUID, prefixID)));
                        ps.executeUpdate();
                    }
                } else {
                    String currentValue = getDynamicValue(conn, playerUUID, prefixID);
                    String approvedValue = ObjectCache.parseApprovedDynamicPrefixValue(currentValue);
                    try (PreparedStatement ps = conn.prepareStatement(dialect.upsertDynamicPrefix())) {
                        ps.setString(1, playerUUID);
                        ps.setString(2, prefixID);
                        ps.setString(3, ObjectCache.markDynamicPrefixDenied(approvedValue, getPendingValue(conn, playerUUID, prefixID)));
                        ps.executeUpdate();
                    }
                }
                try (PreparedStatement ps = conn.prepareStatement("""
                        DELETE FROM mythicprefixes_dynamic_pending
                        WHERE playerUUID = ? AND prefixID = ?
                        """)) {
                    ps.setString(1, playerUUID);
                    ps.setString(2, prefixID);
                    ps.executeUpdate();
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException rollbackEx) {
                        rollbackEx.printStackTrace();
                    }
                }
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    try {
                        conn.setAutoCommit(true);
                        conn.close();
                    } catch (SQLException closeEx) {
                        closeEx.printStackTrace();
                    }
                }
            }
            return false;
        }, DatabaseExecutor.getExecutor());
    }

    @Override
    public CompletableFuture<Void> clearDynamicPrefixValue(String playerUUID, String prefixID) {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement("""
                         DELETE FROM mythicprefixes_dynamic
                         WHERE playerUUID = ? AND prefixID = ?
                         """)) {
                ps.setString(1, playerUUID);
                ps.setString(2, prefixID);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, DatabaseExecutor.getExecutor());
    }

    @Override
    public CompletableFuture<Void> saveDynamicPrefixValue(String playerUUID, String prefixID, String value) {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(dialect.upsertDynamicPrefix())) {
                ps.setString(1, playerUUID);
                ps.setString(2, prefixID);
                ps.setString(3, value);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, DatabaseExecutor.getExecutor());
    }

    private String getPendingValue(Connection conn, String playerUUID, String prefixID) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("""
                SELECT pendingValue
                FROM mythicprefixes_dynamic_pending
                WHERE playerUUID = ? AND prefixID = ?
                """)) {
            ps.setString(1, playerUUID);
            ps.setString(2, prefixID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("pendingValue");
                }
            }
        }
        return "";
    }

    private String getDynamicValue(Connection conn, String playerUUID, String prefixID) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("""
                SELECT approvedValue
                FROM mythicprefixes_dynamic
                WHERE playerUUID = ? AND prefixID = ?
                """)) {
            ps.setString(1, playerUUID);
            ps.setString(2, prefixID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("approvedValue");
                }
            }
        }
        return "";
    }

}
