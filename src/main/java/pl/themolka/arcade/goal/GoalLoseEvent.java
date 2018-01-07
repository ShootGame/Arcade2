package pl.themolka.arcade.goal;

import pl.themolka.arcade.ArcadePlugin;

/**
 * Called when a `Goal` is being lost.
 */
public class GoalLoseEvent extends GoalEvent {
    private final GoalHolder loser;

    private GoalLoseEvent(ArcadePlugin plugin, Goal goal, GoalHolder loser) {
        super(plugin, goal);

        this.loser = loser;
    }

    public GoalHolder getLoser() {
        return this.loser;
    }

    public boolean hasLoser() {
        return this.loser != null;
    }

    public static GoalLoseEvent call(ArcadePlugin plugin, Goal goal) {
        return call(plugin, goal, null);
    }

    public static GoalLoseEvent call(ArcadePlugin plugin, Goal goal, GoalHolder loser) {
        return plugin.getEventBus().postEvent(new GoalLoseEvent(plugin, goal, loser));
    }
}
