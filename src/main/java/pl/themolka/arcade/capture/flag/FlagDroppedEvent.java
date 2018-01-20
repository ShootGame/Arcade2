package pl.themolka.arcade.capture.flag;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.capture.flag.state.DroppedState;
import pl.themolka.arcade.capture.flag.state.FlagState;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.game.GamePlayer;

public class FlagDroppedEvent extends FlagStateEvent implements Cancelable {
    private boolean cancel;
    private final GamePlayer dropper;

    public FlagDroppedEvent(ArcadePlugin plugin, Flag flag, FlagState oldState, DroppedState newState, GamePlayer dropper) {
        super(plugin, flag, oldState, newState);

        this.dropper = dropper;
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }

    public GamePlayer getDropper() {
        return this.dropper;
    }
}
