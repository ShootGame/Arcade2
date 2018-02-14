package pl.themolka.arcade.goal;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.Participator;

/**
 * Called when a `Goal` is being lost.
 */
public class GoalLoseEvent extends GoalEvent {
    private final Participator loser;

    private GoalLoseEvent(ArcadePlugin plugin, Goal goal, Participator loser) {
        super(plugin, goal);

        this.loser = loser;
    }

    public Participator getLoser() {
        return this.loser;
    }

    public boolean hasLoser() {
        return this.loser != null;
    }

    public static GoalLoseEvent call(ArcadePlugin plugin, Goal goal) {
        return call(plugin, goal, null);
    }

    public static GoalLoseEvent call(ArcadePlugin plugin, Goal goal, Participator loser) {
        return plugin.getEventBus().postEvent(new GoalLoseEvent(plugin, goal, loser));
    }
}
