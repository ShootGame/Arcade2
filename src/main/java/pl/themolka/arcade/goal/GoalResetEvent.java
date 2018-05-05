package pl.themolka.arcade.goal;

/**
 * Called when a `Goal` is being reset.
 */
public class GoalResetEvent extends GoalEvent {
    private GoalResetEvent(Goal goal) {
        super(goal);
    }

    public static GoalResetEvent call(Goal goal) {
        return goal.getPlugin().getEventBus().postEvent(new GoalResetEvent(goal));
    }
}
