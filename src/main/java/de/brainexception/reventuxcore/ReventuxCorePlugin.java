package de.brainexception.reventuxcore;

import org.bukkit.plugin.java.JavaPlugin;

public class ReventuxCorePlugin extends JavaPlugin {

    private static ReventuxCorePlugin instance = null;

    @Override
    public void onEnable() {
        getLogger().info("onEnabled called.");
    }

    @Override
    public void onDisable() {
        getLogger().info("onDisable called.");
    }

    public static ReventuxCorePlugin getInstance() {
        if (instance == null)
            instance = new ReventuxCorePlugin();
        return instance;
    }

}
