package pl.themolka.arcade.capture.point.state;

import com.google.common.collect.Multimap;
import pl.themolka.arcade.capture.CapturableState;
import pl.themolka.arcade.capture.CaptureGame;
import pl.themolka.arcade.capture.point.Point;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.util.Color;

import java.util.List;

public abstract class PointState extends CapturableState<Point, PointState> {
    protected final Point point;

    public PointState(Point point) {
        super(point.getCaptureGame(), point);

        this.point = point;
    }

    @Override
    public abstract PointState copy();

    public Color getColor() {
        return this.point.getNeutralColor();
    }

    public void heartbeat(long ticks, Match match, Multimap<Participator, GamePlayer> competitors,
                          Multimap<Participator, GamePlayer> dominators, List<Participator> canCapture, Participator owner) {
    }

    public double getProgress() {
        return Goal.PROGRESS_UNTOUCHED;
    }

    @Override
    public abstract String toString();

    public abstract static class Permanent extends PointState {
        public Permanent(Point point) {
            super(point);
        }
    }

    public abstract static class Progress extends PointState implements CapturableState.Progress {
        private double progress;

        public Progress(Point point) {
            this(point, ZERO);
        }

        public Progress(Point point, double progress) {
            super(point);

            this.progress = progress;
        }

        @Override
        public CaptureGame getGame() {
            return this.game;
        }

        @Override
        public Goal getGoal() {
            return this.point;
        }

        @Override
        public double getProgress() {
            return this.progress;
        }

        @Override
        public void setProgress(double progress) {
            if (progress < ZERO) {
                progress = ZERO;
            } else if (progress > DONE) {
                progress = DONE;
            }

            this.progress = progress;
        }

        public void progress() {
            this.progress(Point.HEARTBEAT_INTERVAL);
        }
    }
}
