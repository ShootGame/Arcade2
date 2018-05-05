package pl.themolka.arcade.capture.flag.state;

import org.bukkit.Location;
import pl.themolka.arcade.capture.CapturableState;
import pl.themolka.arcade.capture.CaptureGame;
import pl.themolka.arcade.capture.flag.Flag;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.util.FinitePercentage;

public abstract class FlagState extends CapturableState<Flag, FlagState> {
    protected final Flag flag;

    public FlagState(Flag flag) {
        super(flag.getCaptureGame(), flag);

        this.flag = flag;
    }

    @Override
    public abstract FlagState copy();

    public void heartbeat(long ticks, Match match, Participator owner) {
    }

    @Override
    public abstract String toString();

    public abstract static class Permanent extends FlagState {
        public Permanent(Flag flag) {
            super(flag);
        }
    }

    public abstract static class Progress extends FlagState implements CapturableState.Progress {
        private FinitePercentage progress;

        public Progress(Flag flag) {
            this(flag, ZERO);
        }

        public Progress(Flag flag, FinitePercentage progress) {
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
        public FinitePercentage getProgress() {
            return this.progress;
        }

        @Override
        public void setProgress(FinitePercentage progress) {
            this.progress = progress;
        }

        public void progress() {
            this.progress(Flag.HEARTBEAT_INTERVAL);
        }
    }

    /**
     * The flag is physically located on the map and visible to players.
     */
    public interface PhysicalFlag {
        boolean canPickup(GamePlayer player);

        Location getLocation();
    }

    /**
     * The flag is not visible or not placed on the map.
     */
    public interface VirtualFlag {
    }
}
