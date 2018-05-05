package pl.themolka.arcade.objective.point;

import pl.themolka.arcade.objective.point.state.PointState;

public class PointStateEvent extends PointEvent {
    private final PointState oldState;
    private final PointState newState;

    public PointStateEvent(PointState oldState, PointState newState) {
        super(newState.getObjective());

        this.oldState = oldState;
        this.newState = newState;
    }

    public PointState getOldState() {
        return this.oldState;
    }

    public PointState getNewState() {
        return this.newState;
    }
}
