package pl.themolka.arcade.goal;

import pl.themolka.arcade.util.Color;
import pl.themolka.arcade.util.StringId;

import java.util.List;

public interface GoalHolder extends StringId {
    boolean addGoal(Goal goal);

    default boolean areGoalsCompleted() {
        for (Goal goal : this.getGoals()) {
            if (!goal.isCompleted(this)) {
                return false;
            }
        }

        return true;
    }

    Color getColor();

    List<Goal> getGoals();

    default String getName() {
        return this.getId();
    }

    String getTitle();

    boolean hasGoal(Goal goal);

    boolean removeGoal(Goal goal);

    void sendGoalMessage(String message);
}
