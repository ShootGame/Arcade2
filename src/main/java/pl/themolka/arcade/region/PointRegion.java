package pl.themolka.arcade.region;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import pl.themolka.arcade.game.Game;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PointRegion extends AbstractRegion {
    private final Vector point;
    private final List<Block> blocks;
    private final RegionBounds bounds;

    public PointRegion(Game game, String id, Vector point) {
        super(game, id);

        this.point = point;

        this.blocks = this.createBlocks();
        this.bounds = this.createBounds();
    }

    public PointRegion(PointRegion original) {
        this(original.getGame(), original.getId(), original.getPoint());
    }

    @Override
    public boolean contains(BlockVector vector) {
        return this.containsZero(vector);
    }

    @Override
    public boolean contains(Region region) {
        return false;
    }

    @Override
    public boolean contains(Vector vector) {
        Vector point = this.getPoint();
        return  vector.getBlockX() == point.getBlockX() &&
                vector.getBlockY() == point.getBlockY() &&
                vector.getBlockZ() == point.getBlockZ();
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
    public double getHighestY() {
        return this.getPoint().getY();
    }

    @Override
    public Vector getRandom(Random random, int limit) {
        return this.getPoint();
    }

    public Vector getPoint() {
        return this.point;
    }

    private List<Block> createBlocks() {
        Location location = this.getPoint().toLocation(this.getWorld());
        return Collections.singletonList(location.getBlock());
    }

    private RegionBounds createBounds() {
        return new RegionBounds(this, this.point, this.point);
    }
}
