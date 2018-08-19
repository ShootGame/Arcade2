package pl.themolka.arcade.objective.flag;

import pl.themolka.arcade.objective.flag.state.FlagState;

public class FlagStateEvent extends FlagEvent {
    private final FlagState oldState;
    private final FlagState newState;

    public FlagStateEvent(Flag flag, FlagState oldState, FlagState newState) {
        super(flag);

        this.oldState = oldState;
        this.newState = newState;
    }

    public FlagState getOldState() {
        return this.oldState;
    }

    public FlagState getNewState() {
        return this.newState;
    }
}
