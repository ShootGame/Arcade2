package pl.themolka.arcade.capture.point;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.capture.point.state.PointState;

public class PointStateEvent extends PointEvent {
    private final PointState oldState;
    private PointState newState;

    public PointStateEvent(ArcadePlugin plugin, Point point, PointState oldState, PointState newState) {
        super(plugin, point);

        this.oldState = oldState;
        this.newState = newState;
    }

    public PointState getOldState() {
        return this.oldState;
    }

    public PointState getNewState() {
        return this.newState;
    }

    // Don't set to null - use Cancelable instead.
    public void setNewState(PointState newState) {
        this.newState = newState;
    }
}
