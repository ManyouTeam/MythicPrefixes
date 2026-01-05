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
    public void needExtraDownload(String jdbcUrl) {}
}
