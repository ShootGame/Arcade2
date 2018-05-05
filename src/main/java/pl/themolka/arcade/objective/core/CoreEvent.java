package pl.themolka.arcade.objective.core;

import pl.themolka.arcade.objective.ObjectiveEvent;

public class CoreEvent extends ObjectiveEvent {
    public CoreEvent(Core core) {
        super(core);
    }

    @Override
    public Core getGoal() {
        return (Core) super.getGoal();
    }
}
