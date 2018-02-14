package pl.themolka.arcade.capture.point;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.capture.point.state.CapturedState;
import pl.themolka.arcade.capture.point.state.PointState;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.game.Participator;

public class PointCapturedEvent extends PointStateEvent implements Cancelable {
    private boolean cancel;
    private final Participator oldOwner;
    private final Participator newOwner;

    public PointCapturedEvent(ArcadePlugin plugin, Point point, PointState oldState, CapturedState newState,
                              Participator oldOwner, Participator newOwner) {
        super(plugin, point, oldState, newState);

        this.oldOwner = oldOwner;
        this.newOwner = newOwner;
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }

    public Participator getOldOwner() {
        return this.oldOwner;
    }

    public Participator getNewOwner() {
        return this.newOwner;
    }

    public boolean hadOwner() {
        return this.oldOwner != null;
    }
}
