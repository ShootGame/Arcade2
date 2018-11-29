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

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SmoothSpawnAgent extends PlayerSpawnAgent {
    public SmoothSpawnAgent(Spawn spawn, Player player) {
        super(spawn, player);
    }

    @Override
    public Location spawn() {
        Player player = this.getPlayer();
        Location from = this.getEntityLocation();
        Location to = this.getLocation();

        if (this.canSpawn(player) && to != null) {
            Vector vector = to.toVector().subtract(from.toVector());
            float yaw = to.getYaw() - from.getYaw();
            float pitch = to.getPitch() - from.getPitch();

            boolean ok = player.teleportRelative(this.normalize(vector), yaw, pitch, this.cause());

            player.setFallDistance(0F); // reset fall distance
            return ok ? to : null;
        }

        return null;
    }

    //
    // Instancing
    //

    public static SmoothSpawnAgent create(Spawn spawn, Player player, Direction yaw, Direction pitch) {
        SmoothSpawnAgent agent = new SmoothSpawnAgent(spawn, player);
        agent.yawDirection = yaw;
        agent.pitchDirection = pitch;
        return agent;
    }
}
