package pl.themolka.arcade.region;

import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import pl.themolka.arcade.map.ArcadeMap;

import java.util.Random;

public class SphereRegion extends AbstractRegion {
    private final RegionBounds bounds;
    private final Vector center;
    private final double radius;

    public SphereRegion(String id, ArcadeMap map, Vector center, double radius) {
        super(id, map);

        this.center = center;
        this.radius = radius;

        this.bounds = this.createBounds();
    }

    public SphereRegion(SphereRegion original) {
        this(original.getId(), original.getMap(), original.getCenter(), original.getRadius());
    }

    @Override
    public boolean contains(BlockVector vector) {
        return this.containsRound(vector);
    }

    @Override
    public boolean contains(Region region) {
        return false;
    }

    @Override
    public boolean contains(Vector vector) {
        return vector.isInSphere(this.getCenter(), this.getRadius());
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
        return this.getCenter().getY() + this.getRadius();
    }

    @Override
    public Vector getRandom(Random random, int limit) {
        for (int i = 0; i < limit; i++) {
            Vector vector = this.getBounds().getRandomVector(random, limit);
            if (vector != null && this.contains(vector)) {
                return vector;
            }
        }

        return null;
    }

    public double getRadius() {
        return this.radius;
    }

    private RegionBounds createBounds() {
        double radius = this.getRadius();
        return new RegionBounds(this,
                this.getCenter().clone().subtract(radius, radius, radius),
                this.getCenter().clone().add(radius, radius, radius));
    }
}
