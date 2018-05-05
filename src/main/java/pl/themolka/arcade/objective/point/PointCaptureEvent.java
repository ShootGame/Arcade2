package pl.themolka.arcade.objective.point;

import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.objective.point.state.Captured;
import pl.themolka.arcade.objective.point.state.PointState;

public class PointCaptureEvent extends PointStateEvent {
    private final Participator oldOwner;
    private final Participator capturer;

    public PointCaptureEvent(PointState oldState, Captured newState, Participator oldOwner, Participator capturer) {
        super(oldState, newState);

        this.oldOwner = oldOwner;
        this.capturer = capturer;
    }

    @Override
    public Captured getNewState() {
        return (Captured) super.getNewState();
    }

    public Participator getOldOwner() {
        return this.oldOwner;
    }

    public Participator getCapturer() {
        return this.capturer;
    }

    public boolean hadOwner() {
        return this.oldOwner != null;
    }
}
