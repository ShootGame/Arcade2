package pl.themolka.arcade.region;

import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.util.List;

public class RegionBounds extends CuboidRegion {
    private final Region region;

    public RegionBounds(Region region, Vector min, Vector max) {
        super(region.getGame(), "_" + region.getId() + "-bounds", min, max);

        this.region = region;
    }

    @Override
    public boolean contains(BlockVector vector) {
        return this.getRegion().contains(vector);
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
        return this;
    }

    @Override
    protected RegionBounds createBounds() {
        return null;
    }

    public Region getRegion() {
        return this.region;
    }
}
