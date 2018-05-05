package pl.themolka.arcade.goal;

/**
 * A goal that can be interacted.
 */
public interface InteractableGoal extends Goal {
    GoalContributions getContributions();

    @Override
    default boolean isUntouched() {
        return !this.isCompleted() && this.getContributions().isEmpty();
    }
}
