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
    public void needExtraDownload(String jdbcUrl) {}
}
