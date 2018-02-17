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
     * Normalize the given location so it is safe for players. The location
     * is not safe by default, if the player is spawning on eg. a carpet.
     */
    protected Location normalize(Location location) {
        return location.clone().add(0.0D, 0.1D, 0.0D);
    }

    protected Vector normalize(Vector vector) {
        return vector.clone().add(0.0D, 0.1D, 0.0D);
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
