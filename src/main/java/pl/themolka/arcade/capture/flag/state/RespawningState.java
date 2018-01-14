package pl.themolka.arcade.capture.flag.state;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.Location;
import pl.themolka.arcade.capture.flag.Flag;
import pl.themolka.arcade.time.Time;

public class RespawningState extends FlagState.Progress implements FlagState.VirtualFlag {
    private final Location target; // Region?

    public RespawningState(Flag flag, Location target) {
        super(flag);

        this.target = target;
    }

    @Override
    public FlagState copy() {
        return new RespawningState(this.flag, this.target);
    }

    @Override
    public Time getProgressTime() {
        return null;
    }

    @Override
    public boolean isProgressPositive() {
        return true;
    }

    public Location getTarget() {
        return this.target;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE)
                .append("flag", this.flag)
                .append("target", this.target)
                .build();
    }
}
