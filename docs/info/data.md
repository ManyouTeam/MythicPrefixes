# 📊Data

## Save

We will auto save player data into database when player leave the server.

Data not saved will be lost after server crash, so enable auto save feature if you don't want this.

## Auto Save

You can use auto save feature so that plugin can store plugin data periodically to avoid data loss due to server crashes. It is not recommended to store at a high frequency, as this can cause server lag. You can find the following content in `config.yml` to set this feature:

```yaml
auto-save:
  enabled: true
  hide-message: false
  period-tick: 6000 # In ticks, 20 ticks = 1 second.
```

## Database

You can find the following content in `config.yml` to set this feature:

```yaml
database:
  enabled: false
  jdbc-url: "jdbc:mysql://localhost:3306/ultimateshop?useSSL=false&autoReconnect=true"
  jdbc-class: "com.mysql.cj.jdbc.Driver"
  properties:
    user: root
    password: 123456
```

| Database                    | JDBC URL Example                              | JDBC Class               |
| --------------------------- | --------------------------------------------- | ------------------------ |
| MySQL                       | jdbc:mysql://localhost:3306/ultimateshop      | com.mysql.cj.jdbc.Driver |
| MariaDB (Added in 1.9.0)    | jdbc:mariadb://localhost:3306/ultimateshop    | org.mariadb.jdbc.Driver  |
| PostgreSQL (Added in 1.9.0) | jdbc:postgresql://localhost:5432/ultimateshop | org.postgresql.Driver    |
| SQLLite (Added in 1.9.0)    | jdbc:sqlite:plugins/UltimateShop/data.db      | org.sqlite.JDBC          |
| h2 (Added in 1.9.0)         | jdbc:h2:file:./plugins/UltimateShop/data      | org.h2.Driver            |
