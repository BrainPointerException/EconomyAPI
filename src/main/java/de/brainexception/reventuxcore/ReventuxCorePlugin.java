package de.brainexception.reventuxcore;

import com.google.inject.Inject;
import com.google.inject.Injector;
import de.brainexception.reventuxcore.database.config.DatabaseConfig;
import de.brainexception.reventuxcore.database.MariaDBSourceProvider;
import de.brainexception.reventuxcore.listener.PlayerListener;
import de.brainexception.reventuxcore.module.BinderModule;
import de.brainexception.reventuxcore.user.UserManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class ReventuxCorePlugin extends JavaPlugin {

    private MariaDBSourceProvider dataSource;
    @Inject private PlayerListener playerListener;
    @Inject private UserManager userManager;

    @Override
    public void onEnable() {
        getLogger().info("onEnabled called.");

        BinderModule module = new BinderModule(this);
        Injector injector = module.createInjector();
        injector.injectMembers(this);

        getServer().getPluginManager().registerEvents(playerListener, this);

        DatabaseConfig config = new DatabaseConfig();

        try {
            config.createDefaultConfiguration();
            Map<String, String> propMap = config.readDefaultConfiguration();
            DatabaseConfig.DatabaseSettings settings =
                    new DatabaseConfig.DatabaseSettings(
                            propMap.get("dataSource.serverName"),
                            propMap.get("dataSource.portNumber"),
                            propMap.get("dataSource.databaseName"),
                            propMap.get("dataSource.user"),
                            propMap.get("dataSource.password"),
                            0,
                            100
                    );
            try {
                dataSource = new MariaDBSourceProvider(settings);
            } catch (SQLException e) {
                getLogger().warning("Error while creating a connection!");
                e.printStackTrace();
            }
        } catch (IOException e) {
            getLogger().warning("Could not create the default configuration!");
            e.printStackTrace();
        }

        userManager = new UserManager(this);

    }

    @Override
    public void onDisable() {
        getLogger().info("onDisable called.");
        dataSource.shutdown();
        userManager.clearCache();
    }

    public MariaDBSourceProvider getDataSource() {
        return dataSource;
    }

    public UserManager getUserManager() {
        return userManager;
    }

}
