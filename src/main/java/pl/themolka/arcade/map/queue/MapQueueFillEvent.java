package pl.themolka.arcade.map.queue;

import pl.themolka.arcade.ArcadePlugin;

public class MapQueueFillEvent extends MapQueueEvent {
    public MapQueueFillEvent(ArcadePlugin plugin, MapQueue queue) {
        super(plugin, queue);
    }
}
