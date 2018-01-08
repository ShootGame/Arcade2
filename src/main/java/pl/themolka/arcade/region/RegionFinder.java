package pl.themolka.arcade.region;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bukkit.Location;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

public abstract class RegionFinder {
    public static final RegionFinder EVERYWHERE = new EverywhereFinder();
    public static final RegionFinder EXACT = new ExactFinder();
    public static final RegionFinder NET = new NetFinder();
    public static final RegionFinder NET_ROUND = new NetRoundFinder();
    public static final RegionFinder NOWHERE = new NowhereFinder();

    protected static final ToStringStyle toStringStyle = ToStringStyle.NO_FIELD_NAMES_STYLE;

    @Override
    public String toString() {
        return new ToStringBuilder(this, toStringStyle)
                .append("name", this.getClass().getSimpleName())
                .build();
    }

    public boolean regionContains(Region region, Location location) {
        return this.regionContains(region, location.toVector());
    }

    public boolean regionContains(Region region, BlockVector vector) {
        return this.regionContains(region, (Vector) vector);
    }

    public abstract boolean regionContains(Region region, Vector vector);

    protected BlockVector newBlockVector(Vector source) {
        return new BlockVector(source.getBlockX(), source.getBlockY(), source.getBlockZ());
    }
}

/**
 * Allow all queries.
 */
class EverywhereFinder extends RegionFinder {
    @Override
    public boolean regionContains(Region region, Vector vector) {
        return true;
    }
}

/**
 * Exact position (eg. entities).
 */
class ExactFinder extends RegionFinder {
    @Override
    public boolean regionContains(Region region, Vector vector) {
        return region.contains(vector);
    }
}

/**
 * Hook into the coordinate net (eg. blocks).
 */
class NetFinder extends RegionFinder {
    @Override
    public boolean regionContains(Region region, Vector vector) {
        return region.contains(this.newBlockVector(vector));
    }
}

/**
 * Hook into the coordinate net and round the numbers results.
 */
class NetRoundFinder extends RegionFinder {
    @Override
    public boolean regionContains(Region region, Vector vector) {
        return region.containsRound(this.newBlockVector(vector));
    }
}

/**
 * Deny all queries.
 */
class NowhereFinder extends RegionFinder {
    @Override
    public boolean regionContains(Region region, Vector vector) {
        return false;
    }
}
