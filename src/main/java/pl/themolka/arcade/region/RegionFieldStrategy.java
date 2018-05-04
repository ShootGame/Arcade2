package pl.themolka.arcade.region;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

public abstract class RegionFieldStrategy implements IRegionFieldStrategy {
    public static final IRegionFieldStrategy EVERYWHERE = new EverywhereStrategy();
    public static final IRegionFieldStrategy EXACT = new ExactStrategy();
    public static final IRegionFieldStrategy NET = new NetStrategy();
    public static final IRegionFieldStrategy NET_ROUND = new NetRoundStrategy();
    public static final IRegionFieldStrategy NOWHERE = new NowhereStrategy();

    protected static final ToStringStyle toStringStyle = ToStringStyle.NO_FIELD_NAMES_STYLE;

    @Override
    public String toString() {
        return new ToStringBuilder(this, toStringStyle)
                .append("name", this.getClass().getSimpleName())
                .build();
    }

    protected BlockVector newBlockVector(Vector source) {
        return new BlockVector(source.getBlockX(), source.getBlockY(), source.getBlockZ());
    }
}

/**
 * Allow all queries.
 */
class EverywhereStrategy extends RegionFieldStrategy {
    @Override
    public boolean regionContains(Region region, Vector vector) {
        return true;
    }
}

/**
 * Exact position (eg. entities).
 */
class ExactStrategy extends RegionFieldStrategy {
    @Override
    public boolean regionContains(Region region, Vector vector) {
        return region.contains(vector);
    }
}

/**
 * Hook into the coordinate net (eg. blocks).
 */
class NetStrategy extends RegionFieldStrategy {
    @Override
    public boolean regionContains(Region region, Vector vector) {
        return region.contains(this.newBlockVector(vector));
    }
}

/**
 * Hook into the coordinate net and round the numbers results.
 */
class NetRoundStrategy extends RegionFieldStrategy {
    @Override
    public boolean regionContains(Region region, Vector vector) {
        return region.containsRound(this.newBlockVector(vector));
    }
}

/**
 * Deny all queries.
 */
class NowhereStrategy extends RegionFieldStrategy {
    @Override
    public boolean regionContains(Region region, Vector vector) {
        return false;
    }
}
