package de.brainexception.reventuxcore;

import com.google.inject.Inject;
import com.google.inject.Injector;
import de.brainexception.reventuxcore.database.DataSource;
import de.brainexception.reventuxcore.listener.PlayerListener;
import de.brainexception.reventuxcore.module.BinderModule;
import de.brainexception.reventuxcore.user.UserManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.sql.SQLException;

public class ReventuxCorePlugin extends JavaPlugin {

    @Inject private DataSource dataSource;
    @Inject private PlayerListener playerListener;
    @Inject private UserManager userManager;

    @Override
    public void onEnable() {
        getLogger().info("onEnabled called.");

        BinderModule module = new BinderModule(this);
        Injector injector = module.createInjector();
        injector.injectMembers(this);

        getServer().getPluginManager().registerEvents(playerListener, this);

        try {
            dataSource = new DataSource(this);
            getLogger().info("Successfully connected to the database.");
        } catch (IOException e) {
            getLogger().warning("Error while creating JDBC connection!");
            getLogger().warning(e.getMessage());
            e.printStackTrace();
        }
        try {
            dataSource.createUserTable();
        } catch (SQLException throwables) {
            getLogger().warning("Error while creating users table!");
            throwables.printStackTrace();
        }

        userManager = new UserManager(this);

        new BukkitRunnable() {
            @Override
            public void run() {
                getLogger().info(userManager.userCache.stats().toString());
                getLogger().info(String.valueOf(userManager.userCache.estimatedSize()));
            }
        }.runTaskTimerAsynchronously(this, 0L, 20L);

    }

    @Override
    public void onDisable() {
        getLogger().info("onDisable called.");
        userManager.clearCache();
        try {
            dataSource.getConnection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public UserManager getUserManager() {
        return userManager;
    }

}
