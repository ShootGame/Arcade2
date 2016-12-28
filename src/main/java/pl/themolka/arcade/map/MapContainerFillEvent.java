package pl.themolka.arcade.map;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Event;

import java.util.ArrayList;
import java.util.List;

public class MapContainerFillEvent extends Event {
    private final List<MapContainerLoader> mapLoaderList = new ArrayList<>();

    public MapContainerFillEvent(ArcadePlugin plugin) {
        super(plugin);
    }

    public boolean addMapLoader(MapContainerLoader loader) {
        return this.mapLoaderList.add(loader);
    }

    public List<MapContainerLoader> getMapLoaderList() {
        return this.mapLoaderList;
    }
}
