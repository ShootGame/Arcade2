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

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerSpawnAgent extends SpawnAgent {
    private final Player player;

    public PlayerSpawnAgent(Spawn spawn, Player player) {
        super(spawn, player);

        this.player = player;
    }

    @Override
    public boolean canSpawn(Entity entity) {
        return super.canSpawn(entity) && this.player.isOnline();
    }

    public Player getPlayer() {
        return this.player;
    }

    //
    // Instancing
    //

    public static PlayerSpawnAgent create(Spawn spawn, Player player, DirectionTranslator yaw, DirectionTranslator pitch) {
        PlayerSpawnAgent agent = new PlayerSpawnAgent(spawn, player);
        agent.yawDirection = yaw;
        agent.pitchDirection = pitch;
        return agent;
    }
}
