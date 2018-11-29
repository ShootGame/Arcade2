/*
 * Copyright 2018 Aleksander Jagiełło
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.themolka.arcade.goal;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.util.Vector;
import pl.themolka.arcade.ArcadePlugin;
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

    public Firework fireComplete(ArcadePlugin plugin, Location at, Color color) {
        return this.fireComplete(plugin, at, color.getFireworkColor());
    }

    public Firework fireComplete(ArcadePlugin plugin, Location at, org.bukkit.Color color) {
        return FireworkUtils.spawn(plugin, at, FIREWORK_POWER, true,
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

    public Firework fireComplete(ArcadePlugin plugin, Location at, Participator participator) {
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
