package pl.themolka.arcade.region;

import org.bukkit.util.Vector;
import pl.themolka.arcade.map.ArcadeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UnionRegion extends AbstractRegion {
    private final RegionBounds bounds;
    private final Region[] regions;

    public UnionRegion(ArcadeMap map, Region... regions) {
        this(generateId(regions), map, regions);
    }

    public UnionRegion(String id, ArcadeMap map, Region... regions) {
        super(id, map);

        this.regions = regions;

        this.bounds = this.createBounds();
    }

    public UnionRegion(UnionRegion original) {
        this(original.getId(), original.getMap(), original.getRegions());
    }

    @Override
    public boolean contains(Region region) {
        for (Region member : this.getRegions()) {
            if (member.contains(region)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean contains(Vector vector) {
        for (Region member : this.getRegions()) {
            if (member.contains(vector)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public RegionBounds getBounds() {
        return this.bounds;
    }

    @Override
    public Vector getCenter() {
        return this.getBounds().getCenter();
    }

    @Override
    public Vector getRandom(Random random, int limit) {
        for (int i = 0; i < limit; i++) {
            Vector vector = this.getRegions()[random.nextInt(this.getRegions().length)].getRandomVector(random, limit);
            if (vector != null) {
                return vector;
            }
        }

        return null;
    }

    public Region[] getRegions() {
        return this.regions;
    }

    public boolean hasRegion(Region region) {
        return this.hasRegion(region.getId());
    }

    public boolean hasRegion(String id) {
        for (Region region : this.getRegions()) {
            if (region.getId().equals(id)) {
                return true;
            }
        }

        return false;
    }

    private RegionBounds createBounds() {
        Vector min = null;
        Vector max = null;

        for (Region member : this.getRegions()) {
            Vector memberMin = member.getBounds().getMin();
            Vector memberMax = member.getBounds().getMax();

            if (min == null) {
                min = memberMin;
            }
            if (min.getX() > memberMin.getX()) {
                min.setX(memberMin.getX());
            }
            if (min.getY() > memberMin.getY()) {
                min.setY(memberMin.getY());
            }
            if (min.getZ() > memberMin.getZ()) {
                min.setZ(memberMin.getZ());
            }

            if (max == null) {
                max = memberMax;
            }
            if (max.getX() > memberMax.getX()) {
                max.setX(memberMax.getX());
            }
            if (max.getY() > memberMax.getY()) {
                max.setY(memberMax.getY());
            }
            if (max.getZ() > memberMax.getZ()) {
                max.setZ(memberMax.getZ());
            }
        }

        return new RegionBounds(this, min, max);
    }

    //
    // ID generation
    //

    public static String generateId(Region... regions) {
        List<String> results = new ArrayList<>();
        for (Region region : regions) {
            results.add(region.getId());
        }

        return generateId(results.toArray(new String[results.size()]));
    }

    public static String generateId(String... ids) {
        StringBuilder builder = new StringBuilder();
        builder.append("_union[");

        for (int i = 0; i < ids.length; i++) {
            if (i != 0) {
                builder.append(",");
            }
            builder.append(ids[i]);
        }

        return builder.append("]").toString();
    }
}
