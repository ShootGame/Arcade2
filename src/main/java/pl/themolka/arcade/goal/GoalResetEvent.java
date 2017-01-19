package pl.themolka.arcade.goal;

import pl.themolka.arcade.ArcadePlugin;

/**
 * Called when a `Goal` is being reset.
 */
public class GoalResetEvent extends GoalEvent {
    public GoalResetEvent(ArcadePlugin plugin, Goal goal) {
        super(plugin, goal);
    }
}
