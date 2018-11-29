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
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.geometry.Direction;
import org.bukkit.util.Vector;
import pl.themolka.arcade.util.Forwarding;

public abstract class ForwardingSpawn extends Forwarding<Spawn>
                                      implements Spawn {
    @Override
    public String getId() {
        return this.delegate().getId();
    }

    @Override
    public Location getLocation() {
        return this.delegate().getLocation();
    }

    @Override
    public PlayerTeleportEvent.TeleportCause getSpawnCause() {
        return this.delegate().getSpawnCause();
    }

    @Override
    public PlayerTeleportEvent.TeleportCause getSpawnCause(Entity entity) {
        return this.delegate().getSpawnCause(entity);
    }

    @Override
    public Vector getVector() {
        return this.delegate().getVector();
    }

    @Override
    public World getWorld() {
        return this.delegate().getWorld();
    }

    //
    // Directional
    //

    @Override
    public Direction createDirection() {
        return this.delegate().createDirection();
    }

    @Override
    public float getYaw() {
        return this.delegate().getYaw();
    }

    @Override
    public float getPitch() {
        return this.delegate().getPitch();
    }

    public interface Config<T extends ForwardingSpawn> extends Spawn.Config<T> {
    }
}
