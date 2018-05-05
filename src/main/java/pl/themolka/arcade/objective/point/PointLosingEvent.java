package pl.themolka.arcade.objective.point;

import pl.themolka.arcade.objective.point.state.Losing;
import pl.themolka.arcade.objective.point.state.PointState;

public class PointLosingEvent extends PointStateEvent {
    public PointLosingEvent(PointState oldState, Losing newState) {
        super(oldState, newState);
    }

    @Override
    public Losing getNewState() {
        return (Losing) super.getNewState();
    }
}
