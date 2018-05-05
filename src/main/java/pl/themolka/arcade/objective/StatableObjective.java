package pl.themolka.arcade.objective;

import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.util.state.ObjectState;
import pl.themolka.arcade.util.state.Statable;

import java.util.logging.Level;

public abstract class StatableObjective<T extends ObjectiveState<?>> extends Objective
                                                                     implements Statable<T> {
    private final ObjectState<T> state;

    protected StatableObjective(Game game, IGameConfig.Library library, Config<?> config) {
        super(game, library, config);

        this.state = ObjectState.defined(this.defineInitialState());
    }

    @Override
    public T getState() {
        return this.state.getState();
    }

    @Override
    public boolean transform(T newState) {
        this.getPlugin().getLogger().log(Level.INFO, this.getName() + " is transforming from " +
                this.formatStateName(this.getState()) + " to " + this.formatStateName(newState) + "...");

        return this.state.transform(newState);
    }

    public abstract T defineInitialState();

    private String formatStateName(T state) {
        return state != null ? state.getStateName() : "unknown";
    }

    public interface Config<T extends StatableObjective> extends Objective.Config<T> {
    }
}
