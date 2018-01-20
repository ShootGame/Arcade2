package pl.themolka.arcade.capture.flag;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.capture.flag.state.CarryingState;
import pl.themolka.arcade.capture.flag.state.FlagState;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.game.GamePlayer;

public class FlagCarryingEvent extends FlagStateEvent implements Cancelable {
    private boolean cancel;
    private final GamePlayer carrier;

    public FlagCarryingEvent(ArcadePlugin plugin, Flag flag, FlagState oldState, CarryingState newState) {
        super(plugin, flag, oldState, newState);

        this.carrier = newState.getCarrier();
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }

    public GamePlayer getCarrier() {
        return this.carrier;
    }
}
