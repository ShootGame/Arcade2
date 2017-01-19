package pl.themolka.arcade.event;

import org.bukkit.Server;
import pl.themolka.arcade.ArcadePlugin;

/**
 * Base class for all events in Arcade.
 */
public class Event {
    private final ArcadePlugin plugin;

    public Event(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    public String getEventName() {
        return this.getClass().getName();
    }

    public ArcadePlugin getPlugin() {
        return this.plugin;
    }

    public Server getServer() {
        return this.getPlugin().getServer();
    }

    public boolean isAsync() {
        return false;
    }
}
