package pl.themolka.arcade.goal;

import java.util.List;

public interface GoalHolder {
    boolean addGoal(Goal goal);

    default boolean areGoalsCompleted() {
        for (Goal goal : this.getGoals()) {
            if (!goal.isCompleted(this)) {
                return false;
            }
        }

        return true;
    }

    int countGoals();

    List<Goal> getGoals();

    boolean hasAnyGoals();

    boolean hasGoal(Goal goal);

    boolean removeGoal(Goal goal);
}
