package pl.themolka.arcade.region;

import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.List;

public class RegionBounds extends CuboidRegion {
    private final Region region;

    public RegionBounds(Region region, Vector min, Vector max) {
        super(region.getId() + "-bounds", region.getMap(), min, max);

        this.region = region;
    }

    @Override
    public boolean contains(Region region) {
        return this.getRegion().contains(region);
    }

    @Override
    public boolean contains(Vector vector) {
        return this.getRegion().contains(vector);
    }

    @Override
    public List<Block> getBlocks() {
        return this.getRegion().getBlocks();
    }

    @Override
    public RegionBounds getBounds() {
        throw new UnsupportedOperationException("Cannot get bounds of bounds.");
    }

    public Region getRegion() {
        return this.region;
    }
}
