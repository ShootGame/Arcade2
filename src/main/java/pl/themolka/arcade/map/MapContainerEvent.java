package pl.themolka.arcade.map;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Event;

public class MapContainerEvent extends Event {
    private final MapContainer container;

    public MapContainerEvent(ArcadePlugin plugin, MapContainer container) {
        super(plugin);

        this.container = container;
    }

    public MapContainer getContainer() {
        return this.container;
    }
}
