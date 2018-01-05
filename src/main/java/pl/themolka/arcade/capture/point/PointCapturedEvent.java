package pl.themolka.arcade.capture.point;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.capture.point.state.CapturedState;
import pl.themolka.arcade.capture.point.state.PointState;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.goal.GoalHolder;

public class PointCapturedEvent extends PointStateEvent implements Cancelable {
    private boolean cancel;
    private final GoalHolder oldOwner;
    private final GoalHolder newOwner;

    public PointCapturedEvent(ArcadePlugin plugin, Point point, PointState oldState, CapturedState newState,
                              GoalHolder oldOwner, GoalHolder newOwner) {
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

    public GoalHolder getOldOwner() {
        return this.oldOwner;
    }

    public GoalHolder getNewOwner() {
        return this.newOwner;
    }

    public boolean hadOwner() {
        return this.oldOwner != null;
    }
}
