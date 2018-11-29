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

package pl.themolka.arcade.spawn;

import org.bukkit.util.Vector;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.region.AbstractRegion;
import pl.themolka.arcade.region.Region;

public class RegionSpawnVector extends AbstractSpawnVector {
    public static final int RANDOM_LIMIT = Integer.MAX_VALUE;

    private final Region region;
    private final float yaw;
    private final float pitch;

    protected RegionSpawnVector(Game game, IGameConfig.Library library, Config config) {
        super(game, config);

        this.region = library.getOrDefine(game, config.region().get());
        this.yaw = config.yaw();
        this.pitch = config.pitch();
    }

    @Override
    public Vector getVector() {
        return this.region.getRandomVector(RANDOM_LIMIT);
    }

    @Override
    public float getYaw() {
        return this.yaw;
    }

    @Override
    public float getPitch() {
        return this.pitch;
    }

    public interface Config extends AbstractSpawnVector.Config<RegionSpawnVector>,
                                    Directional.Config {
        Ref<AbstractRegion.Config<?>> region();

        @Override
        default RegionSpawnVector create(Game game, Library library) {
            return new RegionSpawnVector(game, library, this);
        }
    }
}
