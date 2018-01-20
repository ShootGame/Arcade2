package pl.themolka.arcade.capture.flag;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.capture.flag.state.FlagState;
import pl.themolka.arcade.capture.flag.state.RespawningState;
import pl.themolka.arcade.event.Cancelable;

public class FlagRespawningEvent extends FlagStateEvent implements Cancelable {
    private boolean cancel;

    public FlagRespawningEvent(ArcadePlugin plugin, Flag flag, FlagState oldState, RespawningState newState) {
        super(plugin, flag, oldState, newState);
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }
}
