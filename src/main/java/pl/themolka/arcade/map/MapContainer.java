package pl.themolka.arcade.map;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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

    public List<OfflineMap> findMap(String query) {
        return this.findMap(query, false);
    }

    public List<OfflineMap> findMap(String query, boolean first) {
        List<OfflineMap> results = new ArrayList<>();

        OfflineMap exact = this.getMap(query);
        if (exact != null) {
            results.add(exact);

            if (first) {
                return results;
            }
        }

        query = query.toLowerCase();

        for (OfflineMap map : this.getMaps()) {
            if (!results.contains(map) && map.getName().equalsIgnoreCase(query)) {
                results.add(map);

                if (first) {
                    return results;
                }
            }
        }

        for (OfflineMap map : this.getMaps()) {
            if (!results.contains(map) && map.getDirectory().getName().equalsIgnoreCase(query)) {
                results.add(map);

                if (first) {
                    return results;
                }
            }
        }

        for (OfflineMap map : this.getMaps()) {
            if (!results.contains(map) && map.getName().toLowerCase().contains(query)) {
                results.add(map);

                if (first) {
                    return results;
                }
            }
        }

        for (OfflineMap map : this.getMaps()) {
            if (!results.contains(map) && map.getDescription() != null && map.getDescription().toLowerCase().contains(query)) {
                results.add(map);

                if (first) {
                    return results;
                }
            }
        }

        return results;
    }

    public OfflineMap findMapFirst(String query) {
        List<OfflineMap> results = this.findMap(query, true);
        if (results.isEmpty()) {
            return null;
        }

        return results.get(0);
    }

    public OfflineMap getMap(String name) {
        return this.maps.get(name);
    }

    public OfflineMap getMapByDirectory(File directory) {
        return this.getMapByDirectory(directory.getName());
    }

    public OfflineMap getMapByDirectory(String directory) {
        for (OfflineMap map : this.getMaps()) {
            if (map.getDirectory().getName().equals(directory)) {
                return map;
            }
        }

        return null;
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
