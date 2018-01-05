package pl.themolka.arcade.capture.point.state;

import com.google.common.collect.Multimap;
import org.bukkit.ChatColor;
import pl.themolka.arcade.capture.CaptureGame;
import pl.themolka.arcade.capture.point.Point;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.time.Time;

public abstract class PointState {
    protected final CaptureGame game;
    protected final Point point;

    public PointState(Point point) {
        this.game = point.getCaptureGame();
        this.point = point;
    }

    public ChatColor getColor() {
        return null;
    }

    public void heartbeat(long ticks, Match match, Multimap<GoalHolder, GamePlayer> competitors,
                          Multimap<GoalHolder, GamePlayer> dominators, GoalHolder owner) {
    }

    public double getProgress() {
        return Goal.PROGRESS_UNTOUCHED;
    }

    public static class Permanent extends PointState {
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

            this.setProgress(this.getProgress() + progressPerHeartbeat);
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
