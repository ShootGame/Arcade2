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

import org.apache.commons.lang3.StringUtils;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.IGameModuleConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SpawnsGame extends GameModule {
    private final Map<String, Spawn> spawns = new HashMap<>();

    protected SpawnsGame(Game game, IGameConfig.Library library, Config config) {
        for (Spawn.Config<?> spawn : config.spawns().get()) {
            String id = spawn.id();
            if (StringUtils.isNotEmpty(id)) {
                this.spawns.put(id, library.getOrDefine(game, spawn));
            }
        }
    }

    public void addSpawn(String id, Spawn spawn) {
        this.spawns.put(id, spawn);
    }

    public Spawn getSpawn(String id) {
        return this.getSpawn(id, null);
    }

    public Spawn getSpawn(String id, Spawn def) {
        return id != null ? this.spawns.getOrDefault(id.trim(), def) : def;
    }

    public Set<String> getSpawnIds() {
        return this.spawns.keySet();
    }

    public List<Spawn> getSpawns() {
        return new ArrayList<>(this.spawns.values());
    }

    public boolean removeSpawn(String id) {
        return this.spawns.remove(id) != null;
    }

    public interface Config extends IGameModuleConfig<SpawnsGame> {
        Ref<List<Spawn.Config<?>>> spawns();

        @Override
        default SpawnsGame create(Game game, Library library) {
            return new SpawnsGame(game, library, this);
        }
    }
}
