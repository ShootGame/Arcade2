package pl.themolka.arcade.goal;

import pl.themolka.arcade.game.Participator;

/**
 * Called when a `Goal` is being completed.
 */
public class GoalCompleteEvent extends GoalEvent {
    private final Participator completer;

    private GoalCompleteEvent(Goal goal, Participator completer) {
        super(goal);

        this.completer = completer;
    }

    public Participator getCompleter() {
        return this.completer;
    }

    public boolean hasCompleter() {
        return this.completer != null;
    }

    public static GoalCompleteEvent call(Goal goal) {
        return call(goal, null);
    }

    public static GoalCompleteEvent call(Goal goal, Participator completer) {
        return goal.getPlugin().getEventBus().postEvent(new GoalCompleteEvent(goal, completer));
    }
}
