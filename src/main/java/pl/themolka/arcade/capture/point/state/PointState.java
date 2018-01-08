package pl.themolka.arcade.capture.point.state;

import com.google.common.collect.Multimap;
import pl.themolka.arcade.capture.CapturableState;
import pl.themolka.arcade.capture.point.Point;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.goal.GoalProgressEvent;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.time.Time;
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

    public void heartbeat(long ticks, Match match, Multimap<GoalHolder, GamePlayer> competitors,
                          Multimap<GoalHolder, GamePlayer> dominators, List<GoalHolder> canCapture, GoalHolder owner) {
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

    public abstract static class Progress extends PointState {
        public static final double ZERO = Goal.PROGRESS_UNTOUCHED;
        public static final double DONE = Goal.PROGRESS_SCORED;

        private double progress;

        public Progress(Point point) {
            this(point, ZERO);
        }

        public Progress(Point point, double progress) {
            super(point);

            this.progress = progress;
        }

        @Override
        public double getProgress() {
            return this.progress;
        }

        public abstract Time getProgressTime();

        public boolean isProgressPositive() {
            return true;
        }

        public void progress() {
            double heartbeatInterval = Point.HEARTBEAT_INTERVAL.toMillis();
            double progressTime = this.getProgressTime().toMillis();

            double progressPerHeartbeat = heartbeatInterval / progressTime;
            if (!this.isProgressPositive()) {
                // make the progress negative
                progressPerHeartbeat = progressPerHeartbeat * -1;
            }

            double oldProgress = this.getProgress();
            GoalProgressEvent.call(this.game.getPlugin(), this.point, oldProgress);

            this.setProgress(oldProgress + progressPerHeartbeat);
        }

        public void setProgress(double progress) {
            if (progress < ZERO) {
                progress = ZERO;
            } else if (progress > DONE) {
                progress = DONE;
            }

            this.progress = progress;
        }
    }
}
