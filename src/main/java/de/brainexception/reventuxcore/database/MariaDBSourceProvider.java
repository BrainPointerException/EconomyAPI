package de.brainexception.reventuxcore.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.brainexception.reventuxcore.database.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class MariaDBSourceProvider extends DataSourceProvider<HikariDataSource> {
    
    public MariaDBSourceProvider(DatabaseConfig.DatabaseSettings config) throws SQLException {
        super(config);
        createUserTable();
    }

    @Override
    protected HikariDataSource init() {
        Properties properties = new Properties();
        properties.setProperty("dataSourceClassName", "org.mariadb.jdbc.MariaDbDataSource");
        properties.setProperty("dataSource.serverName", getConfig().getAddress());
        properties.setProperty("dataSource.portNumber", getConfig().getPort());
        properties.setProperty("dataSource.user", getConfig().getUser());
        properties.setProperty("dataSource.password", getConfig().getPassword());
        properties.setProperty("dataSource.databaseName", getConfig().getDatabase());
        
        HikariConfig config = new HikariConfig(properties);
        config.setConnectionTimeout(300000);
        
        return new HikariDataSource(config);
    }

    @Override
    protected void close(HikariDataSource source) {
        source.close();
    }

    public void createUserTable() {
        try (Connection connection = getSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "CREATE TABLE IF NOT EXISTS "
                             + "reventuxcore_users"
                             + "("
                             + "id_user SERIAL PRIMARY KEY, uuid VARCHAR(36) NOT NULL, name VARCHAR(16), coins DOUBLE(6,0), "
                             + " UNIQUE (uuid)"
                             + ") "
                             + "DEFAULT CHARSET = utf8"
             )) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
