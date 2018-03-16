package pl.themolka.arcade.region;

import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import pl.themolka.arcade.game.Game;

import java.util.Random;

public class CuboidRegion extends AbstractRegion {
    private final RegionBounds bounds;
    private final Vector center;
    private final Vector max;
    private final Vector min;

    public CuboidRegion(Game game, String id, Vector min, Vector max) {
        super(game, id);

        this.max = new Vector(Math.max(max.getX(), min.getX()), Math.max(max.getY(), min.getY()),  Math.max(max.getZ(), min.getZ()));
        this.min = new Vector(Math.min(max.getX(), min.getX()), Math.min(max.getY(), min.getY()), Math.min(max.getZ(), min.getZ()));
        this.center = new Vector(
                (max.getX() - min.getX()) / 2D + min.getX(),
                (max.getY() - min.getY()) / 2D + min.getY(),
                (max.getZ() - min.getZ()) / 2D + min.getZ());

        this.bounds = this.createBounds();
    }

    public CuboidRegion(CuboidRegion original) {
        this(original.getGame(), original.getId(), original.getMax(), original.getMin());
    }

    protected CuboidRegion(Game game, Config config) {
        this(game, config.id(), config.min(), config.max());
    }

    @Override
    public boolean contains(BlockVector vector) {
        return this.containsZero(vector);
    }

    @Override
    public boolean contains(Region region) {
        return false;
    }

    @Override
    public boolean contains(Vector vector) {
        Vector min = this.getMin();
        Vector max = this.getMax();

        if (!this.isYPresent(vector)) {
            min.setY(MIN_HEIGHT);
            max.setY(MAX_HEIGHT);
        }

        return  min.getX() <= vector.getX() && max.getX() >= vector.getX() &&
                min.getY() <= vector.getY() && max.getY() >= vector.getY() &&
                min.getZ() <= vector.getZ() && max.getZ() >= vector.getZ();
    }

    @Override
    public RegionBounds getBounds() {
        return this.bounds;
    }

    @Override
    public Vector getCenter() {
        return this.center;
    }

    @Override
    public double getHighestY() {
        return this.getMax().getY();
    }

    @Override
    public Vector getRandom(Random random, int limit) {
        Vector min = this.getMin();
        Vector max = this.getMax();

        return new Vector(this.nextCoordinate(random, min.getX(), max.getX()),
                          this.nextCoordinate(random, min.getY(), max.getY()),
                          this.nextCoordinate(random, min.getZ(), max.getZ()));
    }

    public Vector getMax() {
        return this.max;
    }

    public Vector getMin() {
        return this.min;
    }

    protected RegionBounds createBounds() {
        return new RegionBounds(this, this.getMin(), this.getMax());
    }

    private double nextCoordinate(Random random, double min, double max) {
        return (max - min) * random.nextDouble() + min;
    }

    public interface Config extends AbstractRegion.Config<CuboidRegion> {
        Vector min();
        Vector max();

        @Override
        default CuboidRegion create(Game game) {
            return new CuboidRegion(game, this);
        }
    }
}
