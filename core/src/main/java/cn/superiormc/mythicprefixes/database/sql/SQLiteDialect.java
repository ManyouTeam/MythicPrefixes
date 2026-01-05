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
    public void needExtraDownload(String jdbcUrl) {}
}
