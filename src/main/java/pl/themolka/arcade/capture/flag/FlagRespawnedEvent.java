package pl.themolka.arcade.capture.flag;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.capture.flag.state.FlagState;
import pl.themolka.arcade.capture.flag.state.SpawnedState;
import pl.themolka.arcade.event.Cancelable;

public class FlagRespawnedEvent extends FlagStateEvent implements Cancelable {
    private boolean cancel;

    public FlagRespawnedEvent(ArcadePlugin plugin, Flag flag, FlagState oldState, SpawnedState newState) {
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
