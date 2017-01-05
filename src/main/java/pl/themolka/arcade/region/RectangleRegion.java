package pl.themolka.arcade.region;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import pl.themolka.arcade.map.ArcadeMap;

public class RectangleRegion extends AbstractRegion {
    private final RegionBounds bounds;
    private final Location center;
    private final Location max;
    private final Location min;

    public RectangleRegion(String id, ArcadeMap map, Location min, Location max) {
        super(id, map);

        this.max = new Location(this.getWorld(),
                Math.max(max.getX(), min.getX()),
                Math.max(max.getY(), min.getZ()),
                Math.max(max.getZ(), min.getZ()));

        this.min = new Location(this.getWorld(),
                Math.min(max.getX(), min.getX()),
                Math.min(max.getY(), min.getZ()),
                Math.min(max.getZ(), min.getZ()));

        this.center = new Location(this.getWorld(),
                (max.getX() - min.getX()) / 2D + min.getX(),
                (max.getY() - min.getY()) / 2D + min.getY(),
                (max.getZ() - min.getZ()) / 2D + min.getZ());

        this.bounds = this.createBounds();
    }

    public RectangleRegion(RectangleRegion original) {
        this(original.getId(), original.getMap(), original.getMax(), original.getMin());
    }

    @Override
    public boolean contains(Region region) {
        return false;
    }

    @Override
    public boolean contains(Vector vector) {
        Location min = this.getMin();
        Location max = this.getMax();

        if (!this.isYPresent(vector)) {
            min.setY(MIN_HEIGHT);
            max.setY(MAX_HEIGHT);
        }

        return vector.isInAABB(min.toVector(), max.toVector());
    }

    @Override
    public RegionBounds getBounds() {
        return this.bounds;
    }

    @Override
    public Location getCenter() {
        return this.center;
    }

    public Location getMax() {
        return this.max;
    }

    public Location getMin() {
        return this.min;
    }

    private RegionBounds createBounds() {
        return new RegionBounds(this, this.getMin(), this.getMax());
    }
}
