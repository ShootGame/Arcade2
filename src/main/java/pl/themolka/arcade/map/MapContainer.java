/*
 * Copyright 2018 Aleksander Jagiełło
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.themolka.arcade.map;

import pl.themolka.arcade.util.Container;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapContainer implements Container<OfflineMap> {
    private final Map<String, OfflineMap> maps = new HashMap<>();

    @Override
    public Class<OfflineMap> getType() {
        return OfflineMap.class;
    }

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
        if (query.equals("*")) {
            return new ArrayList<>(this.getMaps());
        }

        OfflineMap exact = this.getMap(query);
        if (exact != null) {
            return Collections.singletonList(exact);
        }

        query = query.toLowerCase();

        // query map name
        for (OfflineMap map : this.getMaps()) {
            if (map.getName().equalsIgnoreCase(query)) {
                return Collections.singletonList(map);
            }
        }

        // query map directory name
        for (OfflineMap map : this.getMaps()) {
            if (map.getDirectory().getName().equalsIgnoreCase(query)) {
                return Collections.singletonList(map);
            }
        }

        // query map name
        List<OfflineMap> results = new ArrayList<>();
        for (OfflineMap map : this.getMaps()) {
            if (!results.contains(map) && map.getName().toLowerCase().contains(query)) {
                results.add(map);

                if (first) {
                    return results;
                }
            }
        }

        // query world description
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
