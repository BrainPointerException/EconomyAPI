package de.brainexception.reventuxcore;

import de.brainexception.reventuxcore.database.DataSource;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.SQLException;

public class ReventuxCorePlugin extends JavaPlugin {

    private DataSource ds;

    @Override
    public void onEnable() {
        getLogger().info("onEnabled called.");
        try {
            ds = new DataSource(this);
            getLogger().info("Successfully connected to the database.");
        } catch (IOException e) {
            getLogger().warning("Error while creating JDBC connection!");
            getLogger().warning(e.getMessage());
            e.printStackTrace();
        }
        try {
            ds.createUserTable();
        } catch (SQLException throwables) {
            getLogger().warning("Error while creating users table!");
            throwables.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("onDisable called.");
        try {
            ds.getConnection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
