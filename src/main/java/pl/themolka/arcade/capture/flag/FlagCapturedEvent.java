package pl.themolka.arcade.capture.flag;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.capture.flag.state.FlagState;
import pl.themolka.arcade.capture.flag.state.RespawningState;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.game.GamePlayer;

public class FlagCapturedEvent extends FlagStateEvent implements Cancelable {
    private boolean cancel;
    private final GamePlayer capturer;

    public FlagCapturedEvent(ArcadePlugin plugin, Flag flag, FlagState oldState, RespawningState newState, GamePlayer capturer) {
        super(plugin, flag, oldState, newState);

        this.capturer = capturer;
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }

    public GamePlayer getCapturer() {
        return this.capturer;
    }
}
