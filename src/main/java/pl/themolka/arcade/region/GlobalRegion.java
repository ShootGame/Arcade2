package pl.themolka.arcade.region;

import org.apache.commons.lang3.RandomUtils;
import org.bukkit.util.Vector;
import pl.themolka.arcade.map.ArcadeMap;

import java.util.Random;

public class GlobalRegion extends CuboidRegion {
    public static final String REGION_ID = "_global";

    public GlobalRegion(ArcadeMap map) {
        super(REGION_ID, map,
                new Vector(Double.MIN_VALUE, MIN_HEIGHT, Double.MIN_VALUE),
                new Vector(Double.MAX_VALUE, MAX_HEIGHT, Double.MAX_VALUE));
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

    @Override
    public Vector getRandom(Random random, int limit) {
        return new Vector(random.nextDouble(), RandomUtils.nextDouble(MIN_HEIGHT, MAX_HEIGHT), random.nextDouble());
    }
}
