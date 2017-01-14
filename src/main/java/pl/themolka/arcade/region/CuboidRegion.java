package pl.themolka.arcade.region;

import org.bukkit.util.Vector;
import pl.themolka.arcade.map.ArcadeMap;

import java.util.Random;

public class CuboidRegion extends AbstractRegion {
    private final RegionBounds bounds;
    private final Vector center;
    private final Vector max;
    private final Vector min;

    public CuboidRegion(String id, ArcadeMap map, Vector min, Vector max) {
        super(id, map);

        this.max = new Vector(Math.max(max.getX(), min.getX()), Math.max(max.getY(), min.getZ()),  Math.max(max.getZ(), min.getZ()));
        this.min = new Vector(Math.min(max.getX(), min.getX()), Math.min(max.getY(), min.getZ()), Math.min(max.getZ(), min.getZ()));
        this.center = new Vector(
                (max.getX() - min.getX()) / 2D + min.getX(),
                (max.getY() - min.getY()) / 2D + min.getY(),
                (max.getZ() - min.getZ()) / 2D + min.getZ());

        this.bounds = this.createBounds();
    }

    public CuboidRegion(CuboidRegion original) {
        this(original.getId(), original.getMap(), original.getMax(), original.getMin());
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

        return vector.isInAABB(min, max);
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
    public Vector getRandom(Random random, int limit) {
        Vector min = this.getMin();
        Vector max = this.getMax();

        return new Vector(
                this.nextCoordinate(random, min.getX(), max.getX()),
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
}
