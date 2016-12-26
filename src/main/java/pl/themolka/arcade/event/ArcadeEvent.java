package pl.themolka.arcade.event;

import org.bukkit.Server;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.commons.event.Event;

public class ArcadeEvent extends Event {
    private final ArcadePlugin plugin;

    public ArcadeEvent(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean post() {
        throw new UnsupportedOperationException("Not supported here!");
    }

    public ArcadePlugin getPlugin() {
        return this.plugin;
    }

    public Server getServer() {
        return this.getPlugin().getServer();
    }
}
