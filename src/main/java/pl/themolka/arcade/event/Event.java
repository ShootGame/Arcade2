package pl.themolka.arcade.event;

import org.bukkit.Server;
import pl.themolka.arcade.ArcadePlugin;

public class Event extends pl.themolka.commons.event.Event {
    private final ArcadePlugin plugin;

    public Event(ArcadePlugin plugin) {
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
