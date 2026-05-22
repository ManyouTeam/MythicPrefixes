package cn.superiormc.mythicprefixes.database.sql;

public class SQLiteDialect extends DatabaseDialect {

    @Override
    public boolean matches(String jdbcUrl) {
        return jdbcUrl.startsWith("jdbc:sqlite:");
    }

    @Override
    public int maxPoolSize() {
        return 1;
    }

    @Override
    public int minIdle() {
        return 1;
    }

    @Override
    public boolean forceSingleConnection() {
        return true;
    }

    @Override
    public String createPrefixTable() {
        return """
            CREATE TABLE IF NOT EXISTS mythicprefixes (
              playerUUID TEXT PRIMARY KEY,
              prefixID TEXT
            )
            """;
    }

    @Override
    public String upsertPrefix() {
        return """
            INSERT INTO mythicprefixes (playerUUID, prefixID)
            VALUES (?, ?)
            ON CONFLICT(playerUUID)
            DO UPDATE SET
              prefixID = excluded.prefixID
            """;
    }

    @Override
    public String createDynamicPrefixTable() {
        return """
            CREATE TABLE IF NOT EXISTS mythicprefixes_dynamic (
              playerUUID TEXT NOT NULL,
              prefixID TEXT NOT NULL,
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
            ON CONFLICT(playerUUID, prefixID)
            DO UPDATE SET
              approvedValue = excluded.approvedValue
            """;
    }

    @Override
    public String createDynamicPrefixRequestTable() {
        return """
            CREATE TABLE IF NOT EXISTS mythicprefixes_dynamic_pending (
              playerUUID TEXT NOT NULL,
              playerName TEXT,
              prefixID TEXT NOT NULL,
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
            ON CONFLICT(playerUUID, prefixID)
            DO UPDATE SET
              playerName = excluded.playerName,
              pendingValue = excluded.pendingValue
            """;
    }

    @Override
    public String approveDynamicPrefixRequest() {
        return """
            INSERT INTO mythicprefixes_dynamic (playerUUID, prefixID, approvedValue)
            SELECT playerUUID, prefixID, pendingValue
            FROM mythicprefixes_dynamic_pending
            WHERE playerUUID = ? AND prefixID = ?
            ON CONFLICT(playerUUID, prefixID)
            DO UPDATE SET
              approvedValue = excluded.approvedValue
            """;
    }

    @Override
    public void needExtraDownload(String jdbcUrl) {}
}
