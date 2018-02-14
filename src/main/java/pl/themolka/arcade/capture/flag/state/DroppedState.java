package pl.themolka.arcade.capture.flag.state;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.Location;
import pl.themolka.arcade.capture.flag.Flag;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.match.Match;

public class DroppedState extends FlagState.Permanent implements FlagState.PhysicalFlag {
    private final GamePlayer dropper;
    private final Location location;

    public DroppedState(Flag flag, GamePlayer dropper, Location location) {
        super(flag);

        this.dropper = dropper;
        this.location = location;
    }

    @Override
    public boolean canPickup(GamePlayer player) {
        return true;
    }

    @Override
    public FlagState copy() {
        return new DroppedState(this.flag, this.dropper, this.location);
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public void heartbeat(long ticks, Match match, Participator owner) {
    }

    public GamePlayer getDropper() {
        return this.dropper;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE)
                .append("flag", this.flag)
                .append("dropper", this.dropper)
                .append("location", this.location)
                .build();
    }
}
