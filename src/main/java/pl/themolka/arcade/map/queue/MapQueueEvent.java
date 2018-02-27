package pl.themolka.arcade.map.queue;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Event;

public class MapQueueEvent extends Event {
    private final MapQueue queue;

    public MapQueueEvent(ArcadePlugin plugin, MapQueue queue) {
        super(plugin);

        this.queue = queue;
    }

    public MapQueue getQueue() {
        return this.queue;
    }
}
