package pl.themolka.arcade.spawn;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.PlayerApplicable;

public class SpawnApply implements PlayerApplicable {
    private final Spawn spawn;
    private final AgentCreator agentCreator;

    public SpawnApply(Spawn spawn, AgentCreator agentCreator) {
        this.spawn = spawn;
        this.agentCreator = agentCreator;
    }

    @Override
    public void apply(GamePlayer player) {
        this.spawn(player);
    }

    public Spawn getSpawn() {
        return this.spawn;
    }

    public AgentCreator getAgentCreator() {
        return this.agentCreator;
    }

    public Location spawn(GamePlayer player) {
        Player bukkit = player.getBukkit();
        if (bukkit != null && bukkit.isOnline()) {
            AgentCreator agentCreator = this.getAgentCreator();
            SpawnAgent agent = agentCreator.createAgent(this.getSpawn(), player, bukkit);

            if (agent != null) {
                return agent.spawn();
            }
        }

        return null;
    }

    public static SpawnApply parse(String id, SpawnsGame spawns, AgentCreator agentCreator) {
        if (id != null) {
            Spawn spawn = spawns.getSpawn(id.trim());
            return spawn != null ? new SpawnApply(spawn, agentCreator) : null;
        }

        return null;
    }

    public interface AgentCreator {
        SpawnAgent createAgent(Spawn spawn, GamePlayer player, Player bukkit);
    }
}
