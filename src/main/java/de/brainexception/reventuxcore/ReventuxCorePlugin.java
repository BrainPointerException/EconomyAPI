package de.brainexception.reventuxcore;

import org.bukkit.plugin.java.JavaPlugin;

public class ReventuxCorePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("onEnabled called.");
    }

    @Override
    public void onDisable() {
        getLogger().info("onDisable called.");
    }

}
