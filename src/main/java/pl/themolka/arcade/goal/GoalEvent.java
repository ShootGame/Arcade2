package pl.themolka.arcade.goal;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameEvent;

public class GoalEvent extends GameEvent {
    private final Goal goal;

    public GoalEvent(ArcadePlugin plugin, Game game, Goal goal) {
        super(plugin, game);

        this.goal = goal;
    }

    public Goal getGoal() {
        return this.goal;
    }
}
