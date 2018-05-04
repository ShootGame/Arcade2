package pl.themolka.arcade.region;

import org.bukkit.Location;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

public interface IRegionFieldStrategy {
    default boolean regionContains(Region region, Location location) {
        return this.regionContains(region, location.toVector());
    }

    default boolean regionContains(Region region, BlockVector vector) {
        return this.regionContains(region, (Vector) vector);
    }

    boolean regionContains(Region region, Vector vector);
}
