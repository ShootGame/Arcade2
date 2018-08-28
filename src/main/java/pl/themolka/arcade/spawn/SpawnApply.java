package pl.themolka.arcade.spawn;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.PlayerApplicable;

public class SpawnApply implements PlayerApplicable {
    private final Spawn spawn;
    private final AgentFactory agentFactory;

    protected SpawnApply(Game game, IGameConfig.Library library, Config config) {
        this.spawn = library.getOrDefine(game, config.spawn().get());
        this.agentFactory = config.agentFactory().get();
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

    public interface AgentFactory {
        SpawnAgent createAgent(Spawn spawn, GamePlayer player, Player bukkit);
    }

    public interface Config extends IGameConfig<SpawnApply> {
        Ref<Spawn.Config<?>> spawn();
        Ref<AgentFactory> agentFactory();

        @Override
        default SpawnApply create(Game game, Library library) {
            return new SpawnApply(game, library, this);
        }
    }
}
