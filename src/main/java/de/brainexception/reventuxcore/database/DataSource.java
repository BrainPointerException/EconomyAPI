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

public class DataSource {

    private ReventuxCorePlugin plugin;

    private HikariConfig config;
    private HikariDataSource ds;

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
                out.write("dataSourceClassName=org.mariadb.jdbc.MariaDbDataSource");
                out.write(System.lineSeparator());
                out.write("dataSource.user=test");
                out.write(System.lineSeparator());
                out.write("dataSource.password=test");
                out.write(System.lineSeparator());
                out.write("dataSource.databaseName=mydb");
                out.write(System.lineSeparator());
                out.write("dataSource.portNumber=3306");
                out.write(System.lineSeparator());
                out.write("dataSource.serverName=localhost");
            }
            plugin.getLogger().info("Default hikari.properties file created");
        }
    }



}