package pl.themolka.arcade.objective.wool;

import pl.themolka.arcade.objective.ObjectiveEvent;

public class WoolEvent extends ObjectiveEvent {
    public WoolEvent(Wool wool) {
        super(wool);
    }

    @Override
    public Wool getGoal() {
        return (Wool) super.getGoal();
    }
}
