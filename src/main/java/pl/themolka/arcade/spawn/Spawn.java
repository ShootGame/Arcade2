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
import org.bukkit.util.Vector;
import pl.themolka.arcade.config.Unique;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.util.StringId;

public interface Spawn extends Directional, StringId {
    PlayerTeleportEvent.TeleportCause DEFAULT_SPAWN_CAUSE = PlayerTeleportEvent.TeleportCause.ENDER_PEARL;

    default Location getLocation() {
        Vector vector = this.getVector();
        World world = this.getWorld();

        if (vector != null && world != null) {
            return vector.toLocation(world, this.getYaw(), this.getPitch());
        }

        return null;
    }

    default PlayerTeleportEvent.TeleportCause getSpawnCause() {
        return DEFAULT_SPAWN_CAUSE;
    }

    default PlayerTeleportEvent.TeleportCause getSpawnCause(Entity entity) {
        return this.getSpawnCause();
    }

    default Vector getVector() {
        Location location = this.getLocation();
        return location != null ? location.toVector() : null;
    }

    default World getWorld() {
        Location location = this.getLocation();
        return location != null ? location.getWorld() : null;
    }

    interface Config<T extends Spawn> extends IGameConfig<T>, Unique {
    }
}
