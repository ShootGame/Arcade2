package pl.themolka.arcade.region;

import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import pl.themolka.arcade.map.ArcadeMap;

import java.util.Random;

public class CylinderRegion extends AbstractRegion {
    private final RegionBounds bounds;
    private final Vector center;
    private final double height;
    private final double radius;

    public CylinderRegion(String id, ArcadeMap map, Vector center, double height, double radius) {
        super(id, map);

        this.center = center;
        this.height = height;
        this.radius = radius;

        this.bounds = this.createBounds();
    }

    public CylinderRegion(CylinderRegion original) {
        this(original.getId(), original.getMap(), original.getCenter(), original.getHeight(), original.getRadius());
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
        Vector center = this.getCenter();
        double power = Math.pow(vector.getX() - center.getX(), 2) + Math.pow(vector.getZ() - center.getZ(), 2);

        if (!this.isYPresent(vector)) {
            return power <= Math.pow(this.getRadius(), 2);
        }

        return center.getY() <= vector.getY() && this.getHighestY() >= vector.getBlockY() && power <= Math.pow(this.getRadius(), 2);
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
        return this.getCenter().getY() + this.getHeight();
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

    public double getDiameter() {
        return this.getRadius() * 2;
    }

    public double getHeight() {
        return this.height;
    }

    public double getRadius() {
        return this.radius;
    }

    private RegionBounds createBounds() {
        double radius = this.getRadius();
        return new RegionBounds(this,
                this.getCenter().clone().subtract(radius, 0, radius),
                this.getCenter().clone().add(radius, this.getHeight(), radius));
    }
}
