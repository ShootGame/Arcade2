package pl.themolka.arcade.region;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import pl.themolka.arcade.map.ArcadeMap;

public class GlobalRegion extends RectangleRegion {
    public static final String REGION_ID = "_global";

    public GlobalRegion(ArcadeMap map) {
        super(REGION_ID, map,
                new Location(map.getWorld(), Double.MIN_VALUE, MIN_HEIGHT, Double.MIN_VALUE),
                new Location(map.getWorld(), Double.MAX_VALUE, MAX_HEIGHT, Double.MAX_VALUE));
    }

    @Override
    public boolean contains(Region region) {
        return true;
    }

    @Override
    public boolean contains(Vector vector) {
        return true;
    }

    @Override
    public RegionBounds getBounds() {
        return null;
    }
}
