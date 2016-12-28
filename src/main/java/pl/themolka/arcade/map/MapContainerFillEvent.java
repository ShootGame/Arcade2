package pl.themolka.arcade.map;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Event;

import java.util.ArrayList;
import java.util.List;

public class MapContainerFillEvent extends Event {
    private final List<MapContainerLoader> loaderList = new ArrayList<>();

    public MapContainerFillEvent(ArcadePlugin plugin) {
        super(plugin);
    }

    public boolean addLoader(MapContainerLoader loader) {
        return this.loaderList.add(loader);
    }

    public List<MapContainerLoader> getLoaderList() {
        return this.loaderList;
    }
}
