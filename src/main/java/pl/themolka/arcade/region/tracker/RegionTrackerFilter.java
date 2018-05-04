package pl.themolka.arcade.region.tracker;

import org.bukkit.Location;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.region.Region;

import java.util.Objects;

public class RegionTrackerFilter implements PlayerTrackerFilter {
    private final Region region;

    public RegionTrackerFilter(Region region) {
        this.region = Objects.requireNonNull(region, "region cannot be null");
    }

    @Override
    public boolean canTrack(GamePlayer player, Location at) {
        return this.region.contains(at);
    }

    public Region getRegion() {
        return this.region;
    }
}
