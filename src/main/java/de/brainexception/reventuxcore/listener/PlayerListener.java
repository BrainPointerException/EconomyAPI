package de.brainexception.reventuxcore.listener;

import com.google.inject.Inject;
import de.brainexception.reventuxcore.ReventuxCorePlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class PlayerListener implements Listener {

    private ReventuxCorePlugin plugin;

    @Inject
    public PlayerListener(ReventuxCorePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent e) {
        if (!plugin.getUserManager().findUsernameSync(e.getUniqueId()).isPresent())  {
            plugin.getUserManager().createUserSync(e.getUniqueId(), e.getName(), 0);
        }
    }

}
