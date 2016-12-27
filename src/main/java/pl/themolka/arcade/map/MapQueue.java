package pl.themolka.arcade.map;

import java.util.Collection;
import java.util.LinkedList;

public class MapQueue extends LinkedList<OfflineMap> {
    public boolean addMap(OfflineMap map) {
        return this.offerLast(map);
    }

    public OfflineMap getNextMap() {
        return this.peek();
    }

    public boolean hasNextMap() {
        return !this.isEmpty();
    }

    public OfflineMap takeNextMap() {
        return this.poll();
    }

    public void replace(Collection<? extends OfflineMap> queue) {
        this.clear();
        this.addAll(queue);
    }

    public void setNextMap(OfflineMap map) {
        this.setNextMap(map, true);
    }

    public OfflineMap setNextMap(OfflineMap map, boolean force) {
        if (force) {
            if (this.hasNextMap()) {
                return this.set(0, map);
            }
            this.addMap(map);
        } else {
            this.offerFirst(map);
        }

        return null;
    }
}
