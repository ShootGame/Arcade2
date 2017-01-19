package pl.themolka.arcade.region;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import pl.themolka.arcade.map.ArcadeMap;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PointRegion extends AbstractRegion {
    private final Vector point;
    private final List<Block> blocks;
    private final RegionBounds bounds;

    public PointRegion(String id, ArcadeMap map, Vector point) {
        super(id, map);

        this.point = point;

        this.blocks = this.createBlocks();
        this.bounds = this.createBounds();
    }

    public PointRegion(PointRegion original) {
        this(original.getId(), original.getMap(), original.getPoint());
    }

    @Override
    public boolean contains(Region region) {
        return false;
    }

    @Override
    public boolean contains(Vector vector) {
        return false;
    }

    @Override
    public List<Block> getBlocks() {
        return this.blocks;
    }

    @Override
    public RegionBounds getBounds() {
        return this.bounds;
    }

    @Override
    public Vector getCenter() {
        return this.getPoint();
    }

    @Override
    public Vector getRandom(Random random, int limit) {
        return this.getPoint();
    }

    public Vector getPoint() {
        return this.point;
    }

    private List<Block> createBlocks() {
        return Collections.singletonList(new Location(
                this.getWorld(),
                this.getPoint().getX(),
                this.getPoint().getY(),
                this.getPoint().getZ()
        ).getBlock());
    }

    private RegionBounds createBounds() {
        return new PointRegionBounds(this);
    }

    private class PointRegionBounds extends RegionBounds {
        public PointRegionBounds(PointRegion region) {
            super(region, region.getPoint(), region.getPoint());
        }
    }
}