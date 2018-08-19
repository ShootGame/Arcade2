package pl.themolka.arcade.objective.flag;

import pl.themolka.arcade.objective.ObjectiveEvent;

public class FlagEvent extends ObjectiveEvent {
    public FlagEvent(Flag flag) {
        super(flag);
    }

    @Override
    public Flag getGoal() {
        return (Flag) super.getGoal();
    }
}
