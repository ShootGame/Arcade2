package pl.themolka.arcade.map;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapContainer {
    private final Map<String, OfflineMap> maps = new HashMap<>();

    public boolean contains(String name) {
        return this.getMapNames().contains(name);
    }

    public boolean containsMap(OfflineMap map) {
        return this.getMaps().contains(map);
    }

    public OfflineMap getMap(String name) {
        return this.maps.get(name);
    }

    public Set<String> getMapNames() {
        return this.maps.keySet();
    }

    public Collection<OfflineMap> getMaps() {
        return this.maps.values();
    }

    public void register(MapContainer container) {
        Collection<OfflineMap> maps = container.getMaps();
        this.register(maps.toArray(new OfflineMap[maps.size()]));
    }

    public void register(OfflineMap... maps) {
        for (OfflineMap map : maps) {
            if (!this.containsMap(map)) {
                this.maps.put(map.getName(), map);
            }
        }
    }
}
