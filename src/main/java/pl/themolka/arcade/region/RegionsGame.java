package pl.themolka.arcade.region;

import pl.themolka.arcade.game.GameModule;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RegionsGame extends GameModule {
    private final Map<String, Region> regions = new HashMap<>();

    public RegionsGame(List<Region> regions) {
        for (Region region : regions) {
            this.addRegion(region);
        }
    }

    public void addRegion(Region region) {
        this.regions.put(region.getId(), region);
    }

    public Region getKit(String id) {
        return this.getKit(id, null);
    }

    public Region getKit(String id, Region def) {
        return this.regions.getOrDefault(id, def);
    }

    public Set<String> getRegionIds() {
        return this.regions.keySet();
    }

    public Collection<Region> getRegions() {
        return this.regions.values();
    }

    public void removeRegion(Region region) {
        this.removeRegion(region.getId());
    }

    public void removeRegion(String id) {
        this.regions.remove(id);
    }
}
