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

    public static PlayerSpawnAgent create(Spawn spawn, Player player, Direction yaw, Direction pitch) {
        PlayerSpawnAgent agent = new PlayerSpawnAgent(spawn, player);
        agent.yawDirection = yaw;
        agent.pitchDirection = pitch;
        return agent;
    }
}
