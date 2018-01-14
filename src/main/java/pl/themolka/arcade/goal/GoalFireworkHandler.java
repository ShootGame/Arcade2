package pl.themolka.arcade.goal;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.util.Vector;
import pl.themolka.arcade.firework.FireworkHandler;
import pl.themolka.arcade.firework.FireworkUtils;
import pl.themolka.arcade.region.CuboidRegion;
import pl.themolka.arcade.util.Color;

import java.util.Arrays;
import java.util.List;

public class GoalFireworkHandler extends FireworkHandler {
    public static final int DEFAULT_RADIUS = 5;
    public static final int FIREWORK_POWER = 1; // The power scale is overloaded...

    public GoalFireworkHandler(boolean enabled) {
        super(enabled);
    }

    public Firework fireComplete(Location at, Color color) {
        return this.fireComplete(at, color.getFireworkColor());
    }

    public Firework fireComplete(Location at, org.bukkit.Color color) {
        FireworkEffect effect = FireworkEffect.builder()
                .with(FireworkEffect.Type.BALL)
                .withColor(color)
                .withFade(color)
                .withFlicker()
                .build();

        return FireworkUtils.spawn(at, FIREWORK_POWER, effect);
    }

    public Firework fireComplete(Location at, GoalHolder competitor) {
        return this.fireComplete(at, competitor.getColor());
    }

    public List<Location> getRegionCorners(CuboidRegion region) {
        return this.getRegionCorners(region, DEFAULT_RADIUS);
    }

    public List<Location> getRegionCorners(CuboidRegion region, int radius) {
        World world = region.getWorld();
        Vector min = region.getMin();
        Vector max = region.getMax();
        int y = max.getBlockY();

        return Arrays.asList(new Location(world, min.getX() - radius, y, min.getZ() - radius),
                             new Location(world, min.getX() - radius, y, max.getZ() + radius),
                             new Location(world, max.getX() + radius, y, min.getZ() - radius),
                             new Location(world, max.getX() + radius, y, max.getZ() + radius));
    }
}
