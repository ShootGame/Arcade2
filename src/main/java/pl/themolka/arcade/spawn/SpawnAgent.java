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

import org.bukkit.EntityLocation;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

public class SpawnAgent extends ForwardingSpawn implements Directional {
    private final Spawn spawn;
    private final Entity entity;

    protected Direction yawDirection;
    protected Direction pitchDirection;

    public SpawnAgent(Spawn spawn, Entity entity) {
        this.spawn = spawn;
        this.entity = entity;
    }

    @Override
    protected Spawn delegate() {
        return this.getSpawn();
    }

    @Override
    public Location getLocation() {
        Vector vector = this.getVector();
        World world = this.getWorld();

        if (vector != null && world != null) {
            return vector.toLocation(world, this.getYaw(), this.getPitch());
        }

        return null;
    }

    @Override
    public float getYaw() {
        return this.yawDirection.getValue(super.getYaw(), this.getEntityLocation().getYaw());
    }

    @Override
    public float getPitch() {
        return this.pitchDirection.getValue(super.getPitch(), this.getEntityLocation().getPitch());
    }

    public boolean canSpawn(Entity entity) {
        return !entity.isDead();
    }

    public Spawn getSpawn() {
        return this.spawn;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public EntityLocation getEntityLocation() {
        return this.entity.getEntityLocation();
    }

    public Direction getYawDirection() {
        return this.yawDirection;
    }

    public Direction getPitchDirection() {
        return this.pitchDirection;
    }

    public Location spawn() {
        Entity entity = this.getEntity();
        Location to = this.getLocation();

        if (this.canSpawn(entity) && to != null) {
            boolean ok = entity.teleport(this.normalize(to), this.cause());

            entity.setFallDistance(0F); // reset fall distance
            return ok ? to : null;
        }

        return null;
    }

    /**
     * Normalize the given {@link Location} so it is safe for players.
     * The Location is not safe by default, if the player is spawning
     * on eg. a carpet.
     */
    protected Location normalize(Location from) {
        Vector vector = this.normalize(from.toVector());
        return vector.toLocation(from.getWorld(), from.getYaw(), from.getPitch());
    }

    /**
     * Normalize the given {@link Vector} so it is safe for players.
     * The Vector is not safe by default, if the player is spawning
     * on eg. a carpet.
     */
    protected Vector normalize(Vector from) {
        Vector vector = from.clone();
        vector.setX(from.getBlockX() + 0.5D);
        vector.setY(from.getBlockY() + 0.1D);
        vector.setZ(from.getBlockZ() + 0.5D);
        return vector;
    }

    protected PlayerTeleportEvent.TeleportCause cause() {
        PlayerTeleportEvent.TeleportCause cause = this.getSpawnCause(this.entity);
        return cause != null ? cause : DEFAULT_SPAWN_CAUSE;
    }

    //
    // Instancing
    //

    public static SpawnAgent create(Spawn spawn, Entity entity, Direction yaw, Direction pitch) {
        SpawnAgent agent = new SpawnAgent(spawn, entity);
        agent.yawDirection = yaw;
        agent.pitchDirection = pitch;
        return agent;
    }
}
