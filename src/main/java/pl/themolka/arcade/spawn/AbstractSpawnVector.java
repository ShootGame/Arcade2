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

import org.bukkit.World;
import org.bukkit.util.Vector;
import pl.themolka.arcade.game.Game;

public abstract class AbstractSpawnVector extends AbstractSpawn {
    private final World world;

    protected AbstractSpawnVector(Game game, Config<?> config) {
        super(config);
        this.world = game.getWorld();
    }

    @Override
    public abstract Vector getVector();

    @Override
    public abstract float getYaw();

    @Override
    public abstract float getPitch();

    @Override
    public World getWorld() {
        return this.world;
    }

    public interface Config<T extends AbstractSpawnVector> extends AbstractSpawn.Config<T> {
    }
}
