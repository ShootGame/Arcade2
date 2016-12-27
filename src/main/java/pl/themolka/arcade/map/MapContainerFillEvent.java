package pl.themolka.arcade.map;

import pl.themolka.arcade.ArcadePlugin;

public class MapContainerFillEvent extends MapContainerEvent {
    public MapContainerFillEvent(ArcadePlugin plugin, MapContainer container) {
        super(plugin, container);
    }
}
