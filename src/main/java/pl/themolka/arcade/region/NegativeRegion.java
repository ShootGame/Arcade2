package pl.themolka.arcade.region;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class NegativeRegion extends AbstractRegion {
    private final Region region;

    public NegativeRegion(Region region) {
        super(region.getId(), region.getMap());

        this.region = region;
    }

    @Override
    public boolean contains(Region region) {
        return !this.getRegion().contains(region);
    }

    @Override
    public boolean contains(Vector vector) {
        return !this.getRegion().contains(vector);
    }

    @Override
    public RegionBounds getBounds() {
        return this.getRegion().getBounds();
    }

    @Override
    public Location getCenter() {
        return this.getRegion().getCenter();
    }

    public Region getRegion() {
        return this.region;
    }
}
