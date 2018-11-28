package pl.themolka.arcade.goal;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.firework.FireworkHandler;
import pl.themolka.arcade.firework.FireworkUtils;
import pl.themolka.arcade.game.Participator;
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

    public GoalFireworkHandler(Ref<Boolean> enabled) {
        this(enabled.get());
    }

    public Firework fireComplete(Plugin plugin, Location at, Color color) {
        return this.fireComplete(plugin, at, color.getFireworkColor());
    }

    public Firework fireComplete(Plugin plugin, Location at, org.bukkit.Color color) {
        return FireworkUtils.spawn(plugin, at, FIREWORK_POWER, false,
                FireworkEffect.builder().with(FireworkEffect.Type.STAR)
                                        .withColor(color)
                                        .withFlicker()
                                        .withTrail()
                                        .build(),
                FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE)
                                        .withColor(color)
                                        .withFlicker()
                                        .withTrail()
                                        .build());
    }

    public Firework fireComplete(Plugin plugin, Location at, Participator participator) {
        return this.fireComplete(plugin, at, participator.getColor());
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
