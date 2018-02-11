package pl.themolka.arcade.spawn;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.geometry.Direction;
import org.bukkit.util.Vector;
import pl.themolka.arcade.util.Forwarding;

public abstract class ForwardingSpawn extends Forwarding<Spawn> implements Spawn {
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
}
