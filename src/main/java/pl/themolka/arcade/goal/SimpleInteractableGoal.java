package pl.themolka.arcade.goal;

import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.Participator;

public abstract class SimpleInteractableGoal extends SimpleGoal implements InteractableGoal {
    protected final GoalContributions contributions = new GoalContributions();

    @Deprecated
    public SimpleInteractableGoal(Game game, Participator owner) {
        super(game, owner);
    }

    protected SimpleInteractableGoal(Game game, IGameConfig.Library library, Config<?> config) {
        super(game, library, config);
    }

    @Override
    public GoalContributions getContributions() {
        return this.contributions;
    }

    @Override
    public boolean isUntouched() {
        return !this.isCompleted() && (super.isUntouched() || this.contributions.isEmpty());
    }

    public interface Config<T extends SimpleInteractableGoal> extends SimpleGoal.Config<T> {
    }
}
