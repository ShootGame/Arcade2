package pl.themolka.arcade.region;

import org.bukkit.util.Vector;
import pl.themolka.arcade.map.ArcadeMap;

import java.util.Random;

public class PointRegion extends AbstractRegion {
    private final Vector point;

    public PointRegion(String id, ArcadeMap map, Vector point) {
        super(id, map);

        this.point = point;
    }

    public PointRegion(PointRegion original) {
        this(original.getId(), original.getMap(), original.getPoint());
    }

    @Override
    public boolean contains(Region region) {
        return false;
    }

    @Override
    public boolean contains(Vector vector) {
        return false;
    }

    @Override
    public RegionBounds getBounds() {
        return null;
    }

    @Override
    public Vector getCenter() {
        return this.getPoint();
    }

    @Override
    public Vector getRandom(Random random, int limit) {
        return this.getPoint();
    }

    public Vector getPoint() {
        return this.point;
    }
}
