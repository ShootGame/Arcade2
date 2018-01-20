package pl.themolka.arcade.capture.flag;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.capture.flag.state.FlagState;

public class FlagStateEvent extends FlagEvent {
    private final FlagState oldState;
    private FlagState newState;

    public FlagStateEvent(ArcadePlugin plugin, Flag flag, FlagState oldState, FlagState newState) {
        super(plugin, flag);

        this.oldState = oldState;
        this.newState = newState;
    }

    public FlagState getOldState() {
        return this.oldState;
    }

    public FlagState getNewState() {
        return this.oldState;
    }

    // Don't set to null - use Cancelable instead.
    public void setNewState(FlagState newState) {
        this.newState = newState;
    }
}
