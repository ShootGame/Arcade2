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
