package pl.themolka.arcade.team;

import org.bukkit.Location;
import pl.themolka.arcade.region.Region;

public class RegionSpawn implements TeamSpawn {
    private final Region region;

    public RegionSpawn(Region region) {
        this.region = region;
    }

    @Override
    public Location getSpawnLocation() {
        return this.getRegion().getRandomVector().toLocation(this.getRegion().getWorld());
    }

    public Region getRegion() {
        return this.region;
    }
}
