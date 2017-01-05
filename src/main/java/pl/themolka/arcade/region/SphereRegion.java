package pl.themolka.arcade.region;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import pl.themolka.arcade.map.ArcadeMap;

public class SphereRegion extends AbstractRegion {
    private final RegionBounds bounds;
    private final Location center;
    private final double radius;

    public SphereRegion(String id, ArcadeMap map, Location center, double radius) {
        super(id, map);

        this.center = center;
        this.radius = radius;

        this.bounds = this.createBounds();
    }

    public SphereRegion(SphereRegion original) {
        this(original.getId(), original.getMap(), original.getCenter(), original.getRadius());
    }

    @Override
    public boolean contains(Region region) {
        return false;
    }

    @Override
    public boolean contains(Vector vector) {
        return vector.isInSphere(this.getCenter().toVector(), this.getRadius());
    }

    @Override
    public RegionBounds getBounds() {
        return this.bounds;
    }

    @Override
    public Location getCenter() {
        return center;
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
