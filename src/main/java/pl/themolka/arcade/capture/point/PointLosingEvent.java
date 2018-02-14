package pl.themolka.arcade.capture.point;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.capture.point.state.LosingState;
import pl.themolka.arcade.capture.point.state.PointState;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.game.Participator;

public class PointLosingEvent extends PointStateEvent implements Cancelable {
    private boolean cancel;
    private final Participator loser;

    public PointLosingEvent(ArcadePlugin plugin, Point point, PointState oldState, LosingState newState) {
        super(plugin, point, oldState, newState);

        this.loser = newState.getLoser();
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }

    public Participator getLoser() {
        return this.loser;
    }
}
