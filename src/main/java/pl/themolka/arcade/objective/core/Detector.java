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
