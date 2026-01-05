package cn.superiormc.mythicprefixes.database.sql;

public class MySQLDialect extends DatabaseDialect {

    @Override
    public boolean matches(String jdbcUrl) {
        return jdbcUrl.startsWith("jdbc:mysql:") || jdbcUrl.startsWith("jdbc:mariadb:");
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
            ON DUPLICATE KEY UPDATE
              prefixID = VALUES(prefixID)
            """;
    }

    @Override
    public void needExtraDownload(String jdbcUrl) {
        if (jdbcUrl.startsWith("jdbc:mariadb:")) {
            loadDriver("mariadb-java-client",
                    "https://repo1.maven.org/maven2/org/mariadb/jdbc/mariadb-java-client/3.1.4/mariadb-java-client-3.1.4.jar",
                    "org.mariadb.jdbc.Driver");
        }
    }
}
