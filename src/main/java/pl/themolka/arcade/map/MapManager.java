package pl.themolka.arcade.map;

import pl.themolka.arcade.ArcadePlugin;

public class MapManager {
    private final ArcadePlugin plugin;

    private final MapContainer container = new MapContainer();

    public MapManager(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    public MapContainer getContainer() {
        return this.container;
    }
}
