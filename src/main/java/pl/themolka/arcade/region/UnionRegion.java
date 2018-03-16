package pl.themolka.arcade.region;

import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.game.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UnionRegion extends AbstractRegion {
    private final RegionBounds bounds;
    private final Region[] regions;

    public UnionRegion(Game game, Region... regions) {
        this(game, generateId(regions), regions);
    }

    public UnionRegion(Game game, String id, Region... regions) {
        super(game, id);

        this.regions = regions;

        this.bounds = this.createBounds();
    }

    public UnionRegion(UnionRegion original) {
        this(original.getGame(), original.getId(), original.getRegions());
    }

    @Override
    public boolean contains(BlockVector vector) {
        for (Region region : this.regions) {
            if (region.contains(vector)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean contains(Region region) {
        for (Region member : this.regions) {
            if (member.contains(region)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean contains(Vector vector) {
        for (Region member : this.regions) {
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
        RegionBounds bounds = this.getBounds();
        return bounds != null ? bounds.getCenter() : null;
    }

    @Override
    public double getHighestY() {
        double y = 0D;
        for (Region region : this.regions) {
            double regionY = region.getHighestY();
            if (regionY > y) {
                y = regionY;
            }
        }

        return y;
    }

    @Override
    public Vector getRandom(Random random, int limit) {
        for (int i = 0; i < limit; i++) {
            Vector vector = this.regions[random.nextInt(this.regions.length)].getRandomVector(random, limit);
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
        for (Region region : this.regions) {
            if (region.getId().equals(id)) {
                return true;
            }
        }

        return false;
    }

    public boolean isEmpty() {
        return this.regions.length == 0;
    }

    private RegionBounds createBounds() {
        Vector min = null;
        Vector max = null;

        for (Region member : this.regions) {
            RegionBounds bounds = member.getBounds();
            if (bounds == null) {
                continue;
            }

            Vector memberMin = bounds.getMin();
            Vector memberMax = bounds.getMax();

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
            if (max.getX() < memberMax.getX()) {
                max.setX(memberMax.getX());
            }
            if (max.getY() < memberMax.getY()) {
                max.setY(memberMax.getY());
            }
            if (max.getZ() < memberMax.getZ()) {
                max.setZ(memberMax.getZ());
            }
        }

        if (min == null) {
            min = new BlockVector(0, 0, 0);
        }
        if (max == null) {
            max = new BlockVector(0, 0, 0);
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

    public interface Config extends AbstractRegion.Config<UnionRegion> {
        List<Ref<AbstractRegion.Config<AbstractRegion>>> regions();

        @Override
        default UnionRegion create(Game game) {
            List<Region> regions = new ArrayList<>();
            for (Ref<AbstractRegion.Config<AbstractRegion>> region : this.regions()) {
                AbstractRegion.Config<?> config = region.getIfPresent();
                if (config == null) {
                    continue;
                }

                AbstractRegion value = config.create(game);
                if (value != null) {
                    regions.add(value);
                }
            }
            Region[] regionsArray = regions.toArray(new Region[regions.size()]);

            String id = this.id();
            if (id == null) {
                id = generateId(regionsArray);
            }

            return new UnionRegion(game, id, regionsArray);
        }
    }
}
