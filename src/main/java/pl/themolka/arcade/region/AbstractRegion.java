package pl.themolka.arcade.region;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import pl.themolka.arcade.map.ArcadeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class AbstractRegion implements Region {
    public static final int RANDOM_LIMIT = 10;

    private static final Random random = new Random();

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
    public boolean contains(BlockVector vector) {
        return this.contains(new Location(this.getWorld(), vector.getX() + 0.5, vector.getY() + 0.5, vector.getZ() + 0.5));
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
        return this.contains(x, 0, z);
    }

    @Override
    public boolean contains(double x, double y, double z) {
        return this.contains(new Location(this.getWorld(), x, y, z));
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

        for (int x = bounds.getMin().getBlockX(); x < bounds.getMax().getBlockX(); x++) {
            for (int y = bounds.getMin().getBlockY(); y < bounds.getMax().getBlockY(); y++) {
                for (int z = bounds.getMin().getBlockZ(); z < bounds.getMax().getBlockZ(); z++) {
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

    public abstract Vector getRandom(Random random, int limit);

    protected boolean isYPresent(Vector vector) {
        return vector.getY() != MIN_HEIGHT && vector.getY() != MAX_HEIGHT;
    }
}
