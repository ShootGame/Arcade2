package pl.themolka.arcade.goal;

import pl.themolka.arcade.ArcadePlugin;

/**
 * Called when a `Goal` is being registered.
 */
public class GoalCreateEvent extends GoalEvent {
    public GoalCreateEvent(ArcadePlugin plugin, Goal goal) {
        super(plugin, goal);
    }
}
