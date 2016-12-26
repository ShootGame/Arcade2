package pl.themolka.arcade.session;

import com.google.common.eventbus.Subscribe;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.PluginReadyEvent;
import pl.themolka.commons.session.Sessions;

public class ArcadeSessions extends Sessions<ArcadeSession> implements Listener {
    private final ArcadePlugin plugin;

    public ArcadeSessions(ArcadePlugin plugin) {
        this.plugin = plugin;

        plugin.registerListenerObject(this);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.insertSession(this.createSession(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.removeSession(this.getSession(event.getPlayer().getUniqueId()));
    }

    @Subscribe
    public void onPluginReady(PluginReadyEvent event) {
        int i = 0;
        for (Player online : event.getServer().getOnlinePlayers()) {
            this.insertSession(this.createSession(online));
            i++;
        }

        if (i > 0) {
            event.getPlugin().getLogger().info("Registered " + i + " online player(s).");
        }
    }

    public ArcadeSession createSession(Player bukkit) {
        ArcadePlayer player = new ArcadePlayer(bukkit);
        return new ArcadeSession(player);
    }
}
