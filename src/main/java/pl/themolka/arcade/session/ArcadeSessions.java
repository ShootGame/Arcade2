package pl.themolka.arcade.session;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.commons.session.Sessions;

public class ArcadeSessions extends Sessions implements Listener {
    private final ArcadePlugin plugin;

    public ArcadeSessions(ArcadePlugin plugin) {
        this.plugin = plugin;

        plugin.registerListenerObject(this);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        ArcadePlayer player = new ArcadePlayer(event.getPlayer());
        this.insertSession(new ArcadeSession(player));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.removeSession(this.getSession(event.getPlayer().getUniqueId()));
    }
}
