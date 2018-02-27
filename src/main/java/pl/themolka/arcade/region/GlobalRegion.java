package pl.themolka.arcade.region;

import org.apache.commons.lang3.RandomUtils;
import org.bukkit.util.Vector;
import pl.themolka.arcade.game.Game;

import java.util.Random;

public class GlobalRegion extends CuboidRegion {
    public static final String REGION_ID = "_global";

    public GlobalRegion(Game game) {
        super(game, REGION_ID,
                new Vector(Double.MIN_VALUE, MIN_HEIGHT, Double.MIN_VALUE),
                new Vector(Double.MAX_VALUE, MAX_HEIGHT, Double.MAX_VALUE));
    }

    public GlobalRegion(CuboidRegion original) {
        this(original.getGame());
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
