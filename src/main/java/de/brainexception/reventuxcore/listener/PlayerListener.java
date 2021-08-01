package de.brainexception.reventuxcore.listener;

import com.google.inject.Inject;
import de.brainexception.reventuxcore.ReventuxCorePlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    /**
     * Plugin instance
     */
    private final ReventuxCorePlugin plugin;

    @Inject
    public PlayerListener(ReventuxCorePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
        if (!plugin.getUserManager().findUsernameSync(e.getUniqueId()).isPresent())  {
            plugin.getUserManager().createUser(e.getUniqueId(), e.getName(), 0);
        }
    }

    @EventHandler
    public void onQuit (PlayerQuitEvent e) {
        plugin.getUserManager().saveUser(
                plugin.getUserManager().getUser(e.getPlayer().getUniqueId()))
        .thenAccept(user -> plugin.getUserManager().unloadUser(user.get().getUuid()));
    }

}
