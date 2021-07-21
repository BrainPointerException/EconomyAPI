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
        } catch (IOException e) {
            getLogger().warning(e.getMessage());
            e.printStackTrace();
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
