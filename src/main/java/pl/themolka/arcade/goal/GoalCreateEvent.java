package pl.themolka.arcade.goal;

/**
 * Called when a `Goal` is being registered.
 */
public class GoalCreateEvent extends GoalEvent {
    private GoalCreateEvent(Goal goal) {
        super(goal);
    }

    public static GoalCreateEvent call(Goal goal) {
        return goal.getPlugin().getEventBus().postEvent(new GoalCreateEvent(goal));
    }
}
