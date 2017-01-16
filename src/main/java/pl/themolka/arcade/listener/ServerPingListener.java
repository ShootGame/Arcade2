package pl.themolka.arcade.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import pl.themolka.arcade.ArcadePlugin;

public class ServerPingListener implements Listener {
    private final ArcadePlugin plugin;

    public ServerPingListener(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        String description = this.plugin.getServerDescription();
        if (description != null) {
            event.setMotd(description);
        }
    }
}
