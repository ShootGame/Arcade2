package pl.themolka.arcade.capture.flag;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.capture.flag.state.CarryingState;
import pl.themolka.arcade.capture.flag.state.FlagState;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.game.GamePlayer;

public class FlagPickedUpEvent extends FlagStateEvent implements Cancelable {
    private boolean cancel;
    private final GamePlayer picker;

    public FlagPickedUpEvent(ArcadePlugin plugin, Flag flag, FlagState oldState, CarryingState newState) {
        super(plugin, flag, oldState, newState);

        this.picker = newState.getCarrier();
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }

    public GamePlayer getPicker() {
        return this.picker;
    }
}
