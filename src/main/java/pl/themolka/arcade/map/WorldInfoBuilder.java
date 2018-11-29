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

package pl.themolka.arcade.map;

import org.apache.commons.lang3.builder.Builder;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import pl.themolka.arcade.generator.Generator;

public class WorldInfoBuilder implements Builder<WorldInfo> {
    private Difficulty difficulty;
    private World.Environment environment;
    private Generator generator;
    private boolean pvp;
    private RandomSeed randomSeed;
    private Location spawn;
    private MapTime time;

    @Override
    public WorldInfo build() {
        WorldInfo info = new WorldInfo();
        info.setDifficulty(this.difficulty());
        info.setEnvironment(this.environment());
        info.setGenerator(this.generator());
        info.setPvp(this.pvp());
        info.setRandomSeed(this.randomSeed());
        info.setSpawn(this.spawn());
        info.setTime(this.time());
        return info;
    }

    public Difficulty difficulty() {
        return this.difficulty;
    }

    public WorldInfoBuilder difficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
        return this;
    }

    public World.Environment environment() {
        return this.environment;
    }

    public WorldInfoBuilder environment(World.Environment environment) {
        this.environment = environment;
        return this;
    }

    public Generator generator() {
        return this.generator;
    }

    public WorldInfoBuilder generator(Generator generator) {
        this.generator = generator;
        return this;
    }

    public boolean pvp() {
        return this.pvp;
    }

    public WorldInfoBuilder pvp(boolean pvp) {
        this.pvp = pvp;
        return this;
    }

    public RandomSeed randomSeed() {
        return this.randomSeed;
    }

    public WorldInfoBuilder randomSeed(RandomSeed randomSeed) {
        this.randomSeed = randomSeed;
        return this;
    }

    public Location spawn() {
        return this.spawn;
    }

    public WorldInfoBuilder spawn(Location spawn) {
        this.spawn = spawn;
        return this;
    }

    public MapTime time() {
        return this.time;
    }

    public WorldInfoBuilder time(MapTime time) {
        this.time = time;
        return this;
    }
}
