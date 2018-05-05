package pl.themolka.arcade.objective.core;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.util.Vector;
import pl.themolka.arcade.region.CuboidRegion;
import pl.themolka.arcade.region.Region;

public class Detector extends CuboidRegion {
    protected Detector(Core core, Vector min, Vector max) {
        super(core.getGame(), formatRegionId(core), min, max);
    }

    private static String formatRegionId(Core core) {
        return core.getId() + "_detector";
    }

    public static Pair<Vector, Vector> createMinMaxVectors(CuboidRegion cuboid, int level) {
        return createMinMaxVectors(cuboid.getMin(), cuboid.getMax(), level);
    }

    public static Pair<Vector, Vector> createMinMaxVectors(Vector min, Vector max, int level) {
        int distance = level * 4;
        return Pair.of(min.clone().subtract(distance, 0, distance).setY(Region.MIN_HEIGHT),
                       max.clone().add(distance, 0, distance).setY(min.getBlockY() - level));
    }

    //
    // Instancing
    //

    public static Detector create(Core core, Vector min, Vector max) {
        return new Detector(core, min, max);
    }

    public static Detector create(Core core, Pair<Vector, Vector> minMax) {
        return create(core, minMax.getLeft(), minMax.getRight());
    }
}
