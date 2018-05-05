package pl.themolka.arcade.goal;

import pl.themolka.arcade.game.Participator;

/**
 * Called when a `Goal` is being lost.
 */
public class GoalLoseEvent extends GoalEvent {
    private final Participator loser;

    private GoalLoseEvent(Goal goal, Participator loser) {
        super(goal);

        this.loser = loser;
    }

    public Participator getLoser() {
        return this.loser;
    }

    public boolean hasLoser() {
        return this.loser != null;
    }

    public static GoalLoseEvent call(Goal goal) {
        return call(goal, null);
    }

    public static GoalLoseEvent call(Goal goal, Participator loser) {
        return goal.getPlugin().getEventBus().postEvent(new GoalLoseEvent(goal, loser));
    }
}
