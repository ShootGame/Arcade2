package pl.themolka.arcade.capture.flag.state;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.Location;
import pl.themolka.arcade.capture.flag.Flag;

public class SpawnedState extends FlagState.Permanent implements FlagState.PhysicalFlag {
    private final Location location; // Region?

    public SpawnedState(Flag flag, Location location) {
        super(flag);

        this.location = location;
    }

    @Override
    public FlagState copy() {
        return new SpawnedState(this.flag, this.location);
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE)
                .append("flag", this.flag)
                .append("location", this.location)
                .build();
    }
}
