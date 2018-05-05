package pl.themolka.arcade.objective.point;

import pl.themolka.arcade.objective.point.state.Capturing;
import pl.themolka.arcade.objective.point.state.PointState;

public class PointCapturingEvent extends PointStateEvent {
    public PointCapturingEvent(PointState oldState, Capturing newState) {
        super(oldState, newState);
    }

    @Override
    public Capturing getNewState() {
        return (Capturing) super.getNewState();
    }
}
