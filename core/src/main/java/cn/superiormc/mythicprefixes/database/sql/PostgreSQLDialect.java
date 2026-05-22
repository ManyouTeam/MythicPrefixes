package cn.superiormc.mythicprefixes.database.sql;

public class PostgreSQLDialect extends DatabaseDialect {

    @Override
    public boolean matches(String jdbcUrl) {
        return jdbcUrl.startsWith("jdbc:postgresql:");
    }

    @Override
    public int maxPoolSize() {
        return 10;
    }

    @Override
    public int minIdle() {
        return 2;
    }

    @Override
    public String createPrefixTable() {
        return """
            CREATE TABLE IF NOT EXISTS mythicprefixes (
              playerUUID VARCHAR(36) PRIMARY KEY,
              prefixID TEXT
            )
            """;
    }

    @Override
    public String upsertPrefix() {
        return """
            INSERT INTO mythicprefixes (playerUUID, prefixID)
            VALUES (?, ?)
            ON CONFLICT (playerUUID)
            DO UPDATE SET
              prefixID = EXCLUDED.prefixID
            """;
    }

    @Override
    public String createDynamicPrefixTable() {
        return """
            CREATE TABLE IF NOT EXISTS mythicprefixes_dynamic (
              playerUUID VARCHAR(36) NOT NULL,
              prefixID VARCHAR(128) NOT NULL,
              approvedValue TEXT,
              PRIMARY KEY (playerUUID, prefixID)
            )
            """;
    }

    @Override
    public String upsertDynamicPrefix() {
        return """
            INSERT INTO mythicprefixes_dynamic (playerUUID, prefixID, approvedValue)
            VALUES (?, ?, ?)
            ON CONFLICT (playerUUID, prefixID)
            DO UPDATE SET
              approvedValue = EXCLUDED.approvedValue
            """;
    }

    @Override
    public String createDynamicPrefixRequestTable() {
        return """
            CREATE TABLE IF NOT EXISTS mythicprefixes_dynamic_pending (
              playerUUID VARCHAR(36) NOT NULL,
              playerName VARCHAR(16),
              prefixID VARCHAR(128) NOT NULL,
              pendingValue TEXT,
              PRIMARY KEY (playerUUID, prefixID)
            )
            """;
    }

    @Override
    public String upsertDynamicPrefixRequest() {
        return """
            INSERT INTO mythicprefixes_dynamic_pending (playerUUID, playerName, prefixID, pendingValue)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (playerUUID, prefixID)
            DO UPDATE SET
              playerName = EXCLUDED.playerName,
              pendingValue = EXCLUDED.pendingValue
            """;
    }

    @Override
    public String approveDynamicPrefixRequest() {
        return """
            INSERT INTO mythicprefixes_dynamic (playerUUID, prefixID, approvedValue)
            SELECT playerUUID, prefixID, pendingValue
            FROM mythicprefixes_dynamic_pending
            WHERE playerUUID = ? AND prefixID = ?
            ON CONFLICT (playerUUID, prefixID)
            DO UPDATE SET
              approvedValue = EXCLUDED.approvedValue
            """;
    }

    @Override
    public void needExtraDownload(String jdbcUrl) {}
}
