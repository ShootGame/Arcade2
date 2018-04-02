package pl.themolka.arcade.goal;

import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.Participator;

public class NullGoal implements Goal {
    public static final String GOAL_NAME = "Null Goal";

    private final Game game;

    public NullGoal(Game game) {
        this.game = game;
    }

    @Override
    public String getColoredName() {
        return this.getName();
    }

    @Override
    public Game getGame() {
        return this.game;
    }

    @Override
    public String getName() {
        return GOAL_NAME;
    }

    @Override
    public boolean isCompletableBy(GoalHolder completer) {
        return Goal.notCompletable();
    }

    @Override
    public boolean isCompleted() {
        return false;
    }

    @Override
    public void setCompleted(boolean completed, Participator completer) {
    }
}
