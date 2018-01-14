package pl.themolka.arcade.capture.flag.state;

import org.bukkit.Location;
import pl.themolka.arcade.capture.CapturableState;
import pl.themolka.arcade.capture.CaptureGame;
import pl.themolka.arcade.capture.flag.Flag;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.match.Match;

public abstract class FlagState extends CapturableState<Flag, FlagState> {
    protected final Flag flag;

    public FlagState(Flag flag) {
        super(flag.getCaptureGame(), flag);

        this.flag = flag;
    }

    @Override
    public abstract FlagState copy();

    public void heartbeat(long ticks, Match match, GoalHolder owner) {
    }

    @Override
    public abstract String toString();

    public abstract static class Permanent extends FlagState {
        public Permanent(Flag flag) {
            super(flag);
        }
    }

    public abstract static class Progress extends FlagState implements CapturableState.Progress {
        private double progress;

        public Progress(Flag flag) {
            this(flag, ZERO);
        }

        public Progress(Flag flag, double progress) {
            super(flag);

            this.progress = progress;
        }

        @Override
        public CaptureGame getGame() {
            return this.game;
        }

        @Override
        public Goal getGoal() {
            return this.flag;
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
    }

    /**
     * The flag is physically located on the map and visible to players.
     */
    public interface PhysicalFlag {
        Location getLocation();
    }

    /**
     * The flag is not visible or not placed on the map.
     */
    public interface VirtualFlag {
    }
}
