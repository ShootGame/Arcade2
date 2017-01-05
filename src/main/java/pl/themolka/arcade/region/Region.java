package pl.themolka.arcade.region;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import pl.themolka.arcade.map.ArcadeMap;

import java.util.List;
import java.util.Random;

public interface Region {
    double MIN_HEIGHT = 0.0;
    double MAX_HEIGHT = Double.MAX_VALUE;

    boolean contains(Block block);

    boolean contains(BlockVector vector);

    boolean contains(Entity entity);

    boolean contains(Location location);

    boolean contains(Region region);

    boolean contains(Vector vector);

    boolean contains(double x, double z);

    boolean contains(double x, double y, double z);

    boolean contains(int x, int z);

    boolean contains(int x, int y, int z);

    List<Block> getBlocks();

    RegionBounds getBounds();

    Vector getCenter();

    String getId();

    ArcadeMap getMap();

    Vector getRandomVector();

    Vector getRandomVector(int limit);

    Vector getRandomVector(Random random);

    Vector getRandomVector(Random random, int limit);

    World getWorld();
}
