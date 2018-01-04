package pl.themolka.arcade.goal;

import pl.themolka.arcade.ArcadePlugin;

/**
 * Called when a `Goal` is being registered.
 */
public class GoalCreateEvent extends GoalEvent {
    private GoalCreateEvent(ArcadePlugin plugin, Goal goal) {
        super(plugin, goal);
    }

    public static GoalCreateEvent call(ArcadePlugin plugin, Goal goal) {
        return plugin.getEventBus().postEvent(new GoalCreateEvent(plugin, goal));
    }
}
