package pl.themolka.arcade.region;

import org.bukkit.Location;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import pl.themolka.arcade.util.Forwarding;

public abstract  class ForwardningRegionFieldStrategy extends Forwarding<IRegionFieldStrategy>
                                                      implements IRegionFieldStrategy {
    @Override
    public boolean regionContains(Region region, Location location) {
        return this.delegate().regionContains(region, location);
    }

    @Override
    public boolean regionContains(Region region, BlockVector vector) {
        return this.delegate().regionContains(region, vector);
    }

    @Override
    public boolean regionContains(Region region, Vector vector) {
        return this.delegate().regionContains(region, vector);
    }
}
