package de.brainexception.reventuxcore.database;

import com.google.inject.Inject;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.brainexception.reventuxcore.ReventuxCorePlugin;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataSource {

    private final ReventuxCorePlugin plugin;

    private final HikariConfig config;
    private final HikariDataSource ds;

    @Inject
    public DataSource(ReventuxCorePlugin plugin) throws IOException {
        this.plugin = plugin;
        Path dir = Paths.get("plugins/ReventuxCore");
        if (!Files.exists(dir))
            Files.createDirectory(dir);
        Path file = Paths.get("plugins/ReventuxCore/hikari.properties");
        if (!Files.exists(file)) {
            Files.createFile(file);
            try (BufferedWriter out = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                plugin.getLogger().info("Writing default hikari.properties");
                out.write("dataSourceClassName=org.mariadb.jdbc.MariaDbDataSource"  + System.lineSeparator());
                out.write("dataSource.user=test"  + System.lineSeparator());
                out.write("dataSource.password=test" + System.lineSeparator());
                out.write("dataSource.databaseName=mydb" + System.lineSeparator());
                out.write("dataSource.portNumber=3306" + System.lineSeparator());
                out.write("dataSource.serverName=localhost" + System.lineSeparator());
            }
            plugin.getLogger().info("Default hikari.properties file created");
        }
        config = new HikariConfig(file.toString());
        ds = new HikariDataSource(config);
    }

    public void createUserTable() throws SQLException {
        executeUpdateQuery(
                "CREATE TABLE IF NOT EXISTS "
                + "reventuxcore_users"
                + "("
                + "id_user SERIAL PRIMARY KEY, uuid BINARY(16) NOT NULL, name VARCHAR(16), coins DOUBLE(6,0), "
                + " UNIQUE (uuid)"
                + ") "
                + "DEFAULT CHARSET = utf8"
        );
    }

    private int executeUpdateQuery(String query) throws SQLException {
        try (PreparedStatement ps = getConnection().prepareStatement(query)) {
            return ps.executeUpdate();
        }
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

}
