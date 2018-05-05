package pl.themolka.arcade.objective;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.IGameModuleConfig;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.match.MatchGame;
import pl.themolka.arcade.match.MatchModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ObjectivesGame extends GameModule {
    private final Map<String, Objective> byId = new HashMap<>();

    protected ObjectivesGame(Game game, IGameConfig.Library library, Config config) {
        for (Objective.Config<?> objective : config.objectives().get()) {
            this.byId.put(objective.id(), library.getOrDefine(game, objective));
        }
    }

    @Override
    public void onEnable() {
        Match match = ((MatchGame) this.getGame().getModule(MatchModule.class)).getMatch();
        for (Objective objective : this.byId.values()) {
            objective.injectParticipatorResolver(match);
        }
    }

    public Objective getObjective(String id) {
        return this.byId.get(Objects.requireNonNull(id, "id cannot be null"));
    }

    public List<Objective> getObjectives() {
        return new ArrayList<>(this.byId.values());
    }

    public interface Config extends IGameModuleConfig<ObjectivesGame> {
        Ref<Set<Objective.Config<?>>> objectives();

        @Override
        default ObjectivesGame create(Game game, Library library) {
            return new ObjectivesGame(game, library, this);
        }
    }
}
