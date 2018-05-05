package pl.themolka.arcade.capture.flag.state;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.Location;
import pl.themolka.arcade.capture.flag.Flag;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.time.Time;
import pl.themolka.arcade.util.FinitePercentage;

public class TimedDroppedState extends FlagState.Progress implements FlagState.PhysicalFlag {
    public static final FinitePercentage LOST = Progress.ZERO;

    private final DroppedState droppedState;
    private final Time timeout;

    public TimedDroppedState(Flag flag, DroppedState droppedState, Time timeout) {
        super(flag);

        this.droppedState = droppedState;
        this.timeout = timeout;
    }

    @Override
    public boolean canPickup(GamePlayer player) {
        return true;
    }

    @Override
    public FlagState copy() {
        return new TimedDroppedState(this.flag, this.droppedState, this.timeout);
    }

    @Override
    public Location getLocation() {
        return this.droppedState.getLocation();
    }

    @Override
    public Time getProgressTime() {
        return this.timeout;
    }

    @Override
    public void heartbeat(long ticks, Match match, Participator owner) {
        this.droppedState.heartbeat(ticks, match, owner);
        this.progress();

        if (this.getProgress().getValue() <= LOST.getValue()) { // The flag is lost at 0%.
        }
    }

    @Override
    public boolean isProgressPositive() {
        return false;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE)
                .append("progress", this.getProgress())
                .append("droppedState", this.droppedState)
                .append("timeout", this.timeout)
                .build();
    }

    public DroppedState getDroppedState() {
        return this.droppedState;
    }

    public Time getTimeout() {
        return this.timeout;
    }
}
