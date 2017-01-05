package pl.themolka.arcade.region;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import pl.themolka.arcade.game.GameModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RegionsGame extends GameModule {
    private final List<Region> regions = new ArrayList<>();
    private final Map<String, Region> regionsById = new HashMap<>();

    public RegionsGame(List<Region> regions) {
        for (Region region : regions) {
            this.addRegion(region);
        }
    }

    public void addRegion(Region region) {
        this.regions.add(region);
        this.regionsById.put(region.getId(), region);
    }

    public Region getRegion(String id) {
        return this.getRegion(id, null);
    }

    public Region getRegion(String id, Region def) {
        return this.regionsById.getOrDefault(id, def);
    }

    public Set<String> getRegionIds() {
        return this.regionsById.keySet();
    }

    public List<Region> getRegions() {
        return this.regions;
    }

    public void removeRegion(Region region) {
        this.removeRegion(region.getId());
    }

    public void removeRegion(String id) {
        this.regions.remove(this.getRegion(id));
        this.regionsById.remove(id);
    }

    //
    // Fetching regions
    //

    public Region fetch(Block block) {
        return this.fetch(block.getLocation().toBlockVector());
    }

    public Region fetch(BlockVector vector) {
        return this.fetch(new Location(this.getGame().getWorld(), vector.getX() + 0.5, vector.getY() + 0.5, vector.getZ() + 0.5));
    }

    public Region fetch(Entity entity) {
        return this.fetch(entity.getLocation());
    }

    public Region fetch(Location location) {
        return this.fetch(location.toVector());
    }

    public Region fetch(Vector vector) {
        List<Region> results = new ArrayList<>();
        for (Region region : this.getRegions()) {
            if (region.contains(vector)) {
                results.add(region);
            }
        }

        if (results.isEmpty()) {
            return null;
        } else if (results.size() == 1) {
            return results.get(0);
        }

        return new UnionRegion(this.getGame().getMap(), results.toArray(new Region[results.size()]));
    }

    public Region fetch(double x, double z) {
        return this.fetch(x, Region.MIN_HEIGHT, z);
    }

    public Region fetch(double x, double y, double z) {
        return this.fetch(new Location(this.getGame().getWorld(), x, y, z));
    }

    public Region fetch(int x, int z) {
        return this.fetch(x, Region.MIN_HEIGHT, z);
    }

    public Region fetch(int x, int y, int z) {
        return this.fetch(new BlockVector(x, y, z));
    }
}
