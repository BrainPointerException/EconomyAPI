package de.brainexception.reventuxcore.database.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public final class DatabaseConfig {

    private DatabaseSettings mariadb;

    public DatabaseConfig() {}

    public void createDefaultConfiguration() throws IOException {
        Path dir = Paths.get("plugins/EconomyAPI");
        if (!Files.exists(dir))
            Files.createDirectory(dir);
        Path file = Paths.get("plugins/EconomyAPI/hikari.properties");
        if (!Files.exists(file)) {
            Files.createFile(file);
            try (BufferedWriter out = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                out.write("dataSourceClassName=org.mariadb.jdbc.MariaDbDataSource"  + System.lineSeparator());
                out.write("dataSource.user=test"  + System.lineSeparator());
                out.write("dataSource.password=test" + System.lineSeparator());
                out.write("dataSource.databaseName=mydb" + System.lineSeparator());
                out.write("dataSource.portNumber=3306" + System.lineSeparator());
                out.write("dataSource.serverName=localhost" + System.lineSeparator());
            }
        }
    }

    public Map<String, String> readDefaultConfiguration() throws IOException {
        Path file = Paths.get("plugins/EconomyAPI/hikari.properties");
        Map<String, String> propMap = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] prop = line.split("=");
                propMap.put(prop[0], prop[1]);
            }
        }
        return propMap;
    }

    public DatabaseSettings getMariadb() {
        return mariadb;
    }

    public static class DatabaseSettings implements Cloneable {

        private String address;
        private String port;
        private String database;
        private String user;
        private String password;
        private int minConnections;
        private int maxConnection;

        public DatabaseSettings(String address, String port, String database, String user, String password,
                                int minConnections, int maxConnection) {
            this.address = address;
            this.port = port;
            this.database = database;
            this.user = user;
            this.password = password;
            this.minConnections = minConnections;
            this.maxConnection = maxConnection;
        }

        public String getAddress() {
            return address;
        }

        public String getPort() {
            return port;
        }

        public String getDatabase() {
            return database;
        }

        public String getUser() {
            return user;
        }

        public String getPassword() {
            return password;
        }

        public int getMinConnections() {
            return minConnections;
        }

        public int getMaxConnection() {
            return Math.max(maxConnection, 1);
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public void setDatabase(String database) {
            this.database = database;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setMinConnections(int minConnections) {
            this.minConnections = minConnections;
        }

        public void setMaxConnection(int maxConnection) {
            this.maxConnection = Math.max(maxConnection, 1);
        }

        @Override
        public DatabaseSettings clone() {
            return new DatabaseSettings(address, port, database, user, password, minConnections, maxConnection);
        }
    }

}
