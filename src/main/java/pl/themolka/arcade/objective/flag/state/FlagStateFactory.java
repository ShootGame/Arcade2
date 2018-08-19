package pl.themolka.arcade.objective.flag.state;

import pl.themolka.arcade.objective.flag.Flag;
import pl.themolka.arcade.objective.flag.FlagStateEvent;

public class FlagStateFactory {
    private final Flag flag;

    public FlagStateFactory(Flag flag) {
        this.flag = flag;
    }

    public Flag getFlag() {
        return this.flag;
    }

    protected <T extends FlagState> T transform(FlagStateEvent event) {
        Flag flag = event.getGoal();
        flag.getPlugin().getEventBus().publish(event);

        FlagState newState = event.getNewState();
        return flag.transform(newState) ? (T) newState : null;
    }
}
