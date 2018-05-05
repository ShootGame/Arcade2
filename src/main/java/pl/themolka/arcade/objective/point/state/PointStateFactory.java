package pl.themolka.arcade.objective.point.state;

import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.objective.point.Point;
import pl.themolka.arcade.objective.point.PointCaptureEvent;
import pl.themolka.arcade.objective.point.PointCapturingEvent;
import pl.themolka.arcade.objective.point.PointLoseEvent;
import pl.themolka.arcade.objective.point.PointLosingEvent;
import pl.themolka.arcade.objective.point.PointStateEvent;
import pl.themolka.arcade.time.Time;
import pl.themolka.arcade.util.FinitePercentage;
import pl.themolka.arcade.util.Nulls;

public class PointStateFactory {
    private final Point point;

    public PointStateFactory(Point point) {
        this.point = point;
    }

    public Point getPoint() {
        return this.point;
    }

    protected <T extends PointState> T transform(PointStateEvent event) {
        Point point = event.getGoal();
        point.getPlugin().getEventBus().publish(event);

        PointState newState = event.getNewState();
        return point.transform(newState) ? (T) newState : null;
    }

    //
    // Captured State
    //

    public Captured newCaptured(Participator capturer) {
        return this.transform(new PointCaptureEvent(this.point.getState(),
                                                    new Captured(this.point),
                                                    this.point.getOwner(),
                                                    capturer));
    }

    //
    // Capturing State
    //

    public Capturing newCapturing(Participator capturer, FinitePercentage progress) {
        return this.newCapturing(null, capturer, progress);
    }

    public Capturing newCapturing(Time captureTime, Participator capturer, FinitePercentage progress) {
        captureTime = Nulls.defaults(captureTime, this.point.getCaptureTime());

        Capturing capturing = new Capturing(this.point, captureTime, capturer);
        capturing.setProgress(progress);
        return this.transform(new PointCapturingEvent(this.point.getState(),
                                                      capturing));
    }

    //
    // Losing State
    //

    public Losing newLosing(Participator loser, FinitePercentage progress) {
        return this.newLosing(null, loser, progress);
    }

    public Losing newLosing(Time loseTime, Participator loser, FinitePercentage progress) {
        loseTime = Nulls.defaults(loseTime, this.point.getLoseTime());

        Losing losing = new Losing(this.point, loseTime, loser);
        losing.setProgress(progress);
        return this.transform(new PointLosingEvent(this.point.getState(),
                                                   losing));
    }

    //
    // Neutral State
    //

    public Neutral newNeutral(Participator loser) {
        return this.transform(new PointLoseEvent(this.point.getState(),
                                                 new Neutral(this.point),
                                                 loser));
    }
}
