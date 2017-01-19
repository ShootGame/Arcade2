package pl.themolka.arcade.goal;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GameEvent;

/**
 * Base event class for all events related to goals.
 */
public class GoalEvent extends GameEvent {
    private final Goal goal;

    public GoalEvent(ArcadePlugin plugin, Goal goal) {
        super(plugin);

        this.goal = goal;
    }

    public Goal getGoal() {
        return this.goal;
    }
}
