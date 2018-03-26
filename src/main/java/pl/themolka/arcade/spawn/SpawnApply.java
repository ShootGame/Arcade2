package pl.themolka.arcade.spawn;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.PlayerApplicable;

public class SpawnApply implements PlayerApplicable {
    private final Spawn spawn;
    private final AgentFactory agentFactory;

    public SpawnApply(Spawn spawn, AgentFactory agentFactory) {
        this.spawn = spawn;
        this.agentFactory = agentFactory;
    }

    @Override
    public void apply(GamePlayer player) {
        this.spawn(player);
    }

    public Spawn getSpawn() {
        return this.spawn;
    }

    public AgentFactory getAgentFactory() {
        return this.agentFactory;
    }

    public Location spawn(GamePlayer player) {
        Player bukkit = player.getBukkit();
        if (bukkit != null && bukkit.isOnline()) {
            AgentFactory factory = this.getAgentFactory();
            SpawnAgent agent = factory.createAgent(this.getSpawn(), player, bukkit);

            if (agent != null) {
                return agent.spawn();
            }
        }

        return null;
    }

    public static SpawnApply parse(String id, SpawnsGame spawns, AgentFactory factory) {
        if (id != null) {
            Spawn spawn = spawns.getSpawn(id.trim());
            return spawn != null ? new SpawnApply(spawn, factory) : null;
        }

        return null;
    }

    public interface AgentFactory {
        SpawnAgent createAgent(Spawn spawn, GamePlayer player, Player bukkit);
    }
}
