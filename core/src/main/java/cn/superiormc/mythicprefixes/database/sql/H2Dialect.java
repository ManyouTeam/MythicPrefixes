package cn.superiormc.mythicprefixes.database.sql;

public class H2Dialect extends DatabaseDialect {

    @Override
    public boolean matches(String jdbcUrl) {
        return jdbcUrl.startsWith("jdbc:h2:");
    }

    @Override
    public int maxPoolSize() {
        return 5;
    }

    @Override
    public int minIdle() {
        return 1;
    }

    @Override
    public String createPrefixTable() {
        return """
            CREATE TABLE IF NOT EXISTS mythicprefixes (
              playerUUID VARCHAR(36) PRIMARY KEY,
              prefixID VARCHAR
            )
            """;
    }

    @Override
    public String upsertPrefix() {
        return """
            MERGE INTO mythicprefixes (playerUUID, prefixID)
            KEY (playerUUID)
            VALUES (?, ?)
            """;
    }

    @Override
    public String createDynamicPrefixTable() {
        return """
            CREATE TABLE IF NOT EXISTS mythicprefixes_dynamic (
              playerUUID VARCHAR(36) NOT NULL,
              prefixID VARCHAR(128) NOT NULL,
              approvedValue VARCHAR,
              PRIMARY KEY (playerUUID, prefixID)
            )
            """;
    }

    @Override
    public String upsertDynamicPrefix() {
        return """
            MERGE INTO mythicprefixes_dynamic (playerUUID, prefixID, approvedValue)
            KEY (playerUUID, prefixID)
            VALUES (?, ?, ?)
            """;
    }

    @Override
    public String createDynamicPrefixRequestTable() {
        return """
            CREATE TABLE IF NOT EXISTS mythicprefixes_dynamic_pending (
              playerUUID VARCHAR(36) NOT NULL,
              playerName VARCHAR(16),
              prefixID VARCHAR(128) NOT NULL,
              pendingValue VARCHAR,
              PRIMARY KEY (playerUUID, prefixID)
            )
            """;
    }

    @Override
    public String upsertDynamicPrefixRequest() {
        return """
            MERGE INTO mythicprefixes_dynamic_pending (playerUUID, playerName, prefixID, pendingValue)
            KEY (playerUUID, prefixID)
            VALUES (?, ?, ?, ?)
            """;
    }

    @Override
    public String approveDynamicPrefixRequest() {
        return """
            MERGE INTO mythicprefixes_dynamic (playerUUID, prefixID, approvedValue)
            KEY (playerUUID, prefixID)
            SELECT playerUUID, prefixID, pendingValue
            FROM mythicprefixes_dynamic_pending
            WHERE playerUUID = ? AND prefixID = ?
            """;
    }

    @Override
    public void needExtraDownload(String jdbcUrl) {}
}
