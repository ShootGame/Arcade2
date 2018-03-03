package pl.themolka.arcade.region;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import pl.themolka.arcade.game.GameHolder;
import pl.themolka.arcade.util.StringId;

import java.util.List;
import java.util.Random;

public interface Region extends GameHolder, StringId {
    double MIN_HEIGHT = -0.1;
    double MAX_HEIGHT = Double.MAX_VALUE;
    int RANDOM_LIMIT = 10;

    boolean contains(Block block);

    // The implementation should choose between "containsRound(vector)"
    // and "containsZero(vector)" methods.
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

    double getHighestY();

    Vector getRandomVector();

    Vector getRandomVector(int limit);

    Vector getRandomVector(Random random);

    Vector getRandomVector(Random random, int limit);

    World getWorld();

    //
    // BlockVector Comparison
    //

    /*
     * There are two methods of comparing block vectors in the game. First of
     * all we need to understand how blocks and locations are represented. The
     * Minecraft coordinate net is based on double numbers.
     *
     * - Entities utilizes doubles as their location coordinates. This provides
     * opportunity to place entities EXACTLY at the given coordinates
     * independently from the net.
     * - Blocks utilizes integers as their location coordinates. Blocks can only
     * be placed at the static coordinate location.
     *
     * We have to methods of comparing block vectors:
     * - Rounding method for comparison of coordinates NOT HOOK into the block
     * location net.
     * - Zeroing method for comparison of the coordinates HOOKED into the block
     * location net.
     */

    default boolean containsRound(BlockVector vector) {
        return this.contains(new Vector(vector.getBlockX() + 0.5D,
                                        vector.getBlockY() + 0.5D,
                                        vector.getBlockZ() + 0.5D));
    }

    default boolean containsZero(BlockVector vector) {
        return this.contains(new Vector(vector.getBlockX(),
                                        vector.getBlockY(),
                                        vector.getBlockZ()));
    }
}
