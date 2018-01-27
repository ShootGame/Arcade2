package pl.themolka.arcade.region;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import pl.themolka.arcade.filter.FilterSet;
import pl.themolka.arcade.map.ArcadeMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public abstract class AbstractRegion implements Region {
    private static final Random random = new Random();

    private final Map<RegionEventType, FilterSet> filters = new HashMap<>();
    private final String id;
    private final ArcadeMap map;

    public AbstractRegion(String id, ArcadeMap map) {
        this.id = id;
        this.map = map;
    }

    public AbstractRegion(AbstractRegion original) {
        this(original.getId(), original.getMap());
    }

    @Override
    public boolean contains(Block block) {
        return this.contains(block.getLocation().toBlockVector());
    }

    @Override
    public boolean contains(Entity entity) {
        return this.contains(entity.getLocation());
    }

    @Override
    public boolean contains(Location location) {
        return this.contains(location.toVector());
    }

    @Override
    public boolean contains(double x, double z) {
        return this.contains(x, 0D, z);
    }

    @Override
    public boolean contains(double x, double y, double z) {
        return this.contains(new Vector(x, y, z));
    }

    @Override
    public boolean contains(int x, int z) {
        return this.contains(x, 0, z);
    }

    @Override
    public boolean contains(int x, int y, int z) {
        return this.contains(new BlockVector(x, y, z));
    }

    @Override
    public List<Block> getBlocks() {
        List<Block> results = new ArrayList<>();

        RegionBounds bounds = this.getBounds();
        if (bounds == null) {
            return results;
        }

        Vector min = bounds.getMin();
        Vector max = bounds.getMax();

        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    Block block = this.getWorld().getBlockAt(x, y, z);

                    if (this.contains(block)) {
                        results.add(block);
                    }
                }
            }
        }

        return results;
    }

    @Override
    public FilterSet getFilter(RegionEventType event) {
        return this.getFilter(event, null);
    }

    @Override
    public FilterSet getFilter(RegionEventType event, FilterSet def) {
        return this.filters.getOrDefault(event, def);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public ArcadeMap getMap() {
        return this.map;
    }

    @Override
    public Vector getRandomVector() {
        return this.getRandomVector(null);
    }

    @Override
    public Vector getRandomVector(int limit) {
        return this.getRandomVector(null, RANDOM_LIMIT);
    }

    @Override
    public Vector getRandomVector(Random random) {
        return this.getRandomVector(random, RANDOM_LIMIT);
    }

    @Override
    public Vector getRandomVector(Random random, int limit) {
        if (random == null) {
            random = AbstractRegion.random;
        }

        return getRandom(random, limit);
    }

    @Override
    public World getWorld() {
        return this.getMap().getWorld();
    }

    @Override
    public boolean hasFilter(RegionEventType event) {
        return this.filters.containsKey(event);
    }

    @Override
    public void removeFilter(RegionEventType event) {
        this.filters.remove(event);
    }

    @Override
    public void setFilter(RegionEventType event, FilterSet filter) {
        this.filters.put(event, filter);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AbstractRegion && ((AbstractRegion) obj).getId().equals(this.getId());
    }

    protected abstract Vector getRandom(Random random, int limit);

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    protected boolean isYPresent(Vector vector) {
        return vector.getY() != MIN_HEIGHT && vector.getY() != MAX_HEIGHT;
    }
}
