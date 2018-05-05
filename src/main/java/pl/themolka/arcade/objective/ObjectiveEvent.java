package pl.themolka.arcade.objective;

import pl.themolka.arcade.goal.GoalEvent;

public class ObjectiveEvent extends GoalEvent {
    public ObjectiveEvent(Objective objective) {
        super(objective);
    }

    @Override
    public Objective getGoal() {
        return (Objective) super.getGoal();
    }
}
