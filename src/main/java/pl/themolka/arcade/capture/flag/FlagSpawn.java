package pl.themolka.arcade.capture.flag;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Banner;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;
import pl.themolka.arcade.capture.CaptureGame;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.Filters;
import pl.themolka.arcade.region.Region;
import pl.themolka.arcade.region.RegionFieldStrategy;

import java.util.List;
import java.util.Random;

public class FlagSpawn {
    public static final RegionFieldStrategy DEFAULT_FIELD_STRATEGY = RegionFieldStrategy.NET;
    public static final Filter DEFAULT_FILTER = Filters.undefined();

    private final Random random = new Random();

    private final CaptureGame game;
    private final Flag flag;

    private Banner banner;
    private BlockFace direction;
    private RegionFieldStrategy fieldStrategy = DEFAULT_FIELD_STRATEGY;
    private Filter filter = DEFAULT_FILTER;
    private Region region;

    public FlagSpawn(CaptureGame game, Flag flag) {
        this.game = game;
        this.flag = flag;
    }

    public boolean canSpawn() {
        return this.filter.filter(this.flag).isNotFalse();
    }

    public Banner getBanner() {
        return this.banner;
    }

    public BlockFace getDirection() {
        return this.direction;
    }

    public RegionFieldStrategy getFieldStrategy() {
        return this.fieldStrategy;
    }

    public Filter getFilter() {
        return this.filter;
    }

    public Flag getFlag() {
        return this.flag;
    }

    public Region getRegion() {
        return this.region;
    }

    public boolean hasDirection() {
        return this.direction != null;
    }

    public BlockFace nextDirection() {
        List<BlockFace> values = BlockFace.horizontal();
        return values.get(this.random.nextInt(values.size()));
    }

    public BlockFace nextDirectionOrDefault() {
        if (this.hasDirection()) {
            return this.getDirection();
        } else {
            return this.nextDirection();
        }
    }

    public Location nextLocation(int limit) {
        return this.nextLocation(limit, this.nextDirection().yaw());
    }

    public Location nextLocation(int limit, float yaw) {
        Vector next = this.nextVector(limit);
        if (next != null) {
            World world = this.game.getGame().getWorld();
            return new Location(world, next.getBlockX(), next.getBlockY(), next.getBlockZ(), yaw, 0F);
        }

        return null;
    }

    public Location nextLocationOrDefault(int limit) {
        return this.nextLocation(limit, this.nextDirectionOrDefault().yaw());
    }

    public Vector nextVector(int limit) {
        return this.getRegion().getRandomVector(this.random, limit);
    }

    public void setBanner(Banner banner) {
        this.banner = banner;
    }

    public void setDirection(BlockFace direction) {
        this.direction = direction;
    }

    public void setFieldStrategy(RegionFieldStrategy fieldStrategy) {
        this.fieldStrategy = fieldStrategy;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public void setRegion(Region region) {
        this.region = region;
    }
}
