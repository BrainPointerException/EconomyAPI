package de.brainexception.reventuxcore.database;

import de.brainexception.reventuxcore.database.config.DatabaseConfig;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class DataSourceProvider<T extends DataSource> {

    private final T source;
    private final DatabaseConfig.DatabaseSettings config;

    public DataSourceProvider(DatabaseConfig.DatabaseSettings config) throws SQLException {
        this.config = config;
        this.source = init();
        isValid(source);
    }

    protected abstract T init();

    public final void shutdown() {
        close(source);
    }

    public DataSource getSource() {
        return source;
    }

    public DatabaseConfig.DatabaseSettings getConfig() {
        return config;
    }

    protected abstract void close(T source);

    protected boolean isValid(DataSource source) throws SQLException {
        try (Connection connection = source.getConnection()) {
            return connection.isValid(5 * 1000);
        }
    }

}
