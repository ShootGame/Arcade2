package pl.themolka.arcade.capture.point;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.capture.point.state.PointState;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.goal.GoalHolder;

public class PointLostEvent extends PointStateEvent implements Cancelable {
    private boolean cancel;
    private final GoalHolder loser;

    public PointLostEvent(ArcadePlugin plugin, Point point, PointState oldState, PointState newState, GoalHolder loser) {
        super(plugin, point, oldState, newState);

        this.loser = loser;
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }

    public GoalHolder getLoser() {
        return this.loser;
    }
}
