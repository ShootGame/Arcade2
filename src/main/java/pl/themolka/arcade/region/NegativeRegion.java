package pl.themolka.arcade.region;

import org.bukkit.util.Vector;

import java.util.Random;

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
    public Vector getCenter() {
        return this.getRegion().getCenter();
    }

    @Override
    public Vector getRandom(Random random, int limit) {
        return this.getRegion().getRandomVector(random, limit);
    }

    public Region getRegion() {
        return this.region;
    }
}