package pl.themolka.arcade.capture.flag.state;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.Location;
import pl.themolka.arcade.capture.flag.Flag;
import pl.themolka.arcade.game.GamePlayer;

public class CarryingState extends FlagState.Permanent implements FlagState.PhysicalFlag {
    private final GamePlayer carrier;
    private final Location source;

    public CarryingState(Flag flag, GamePlayer carrier, Location source) {
        super(flag);

        this.carrier = carrier;
        this.source = source;
    }

    @Override
    public FlagState copy() {
        return new CarryingState(this.flag, this.carrier, this.source);
    }

    @Override
    public Location getLocation() {
        return this.getCarrier().getBukkit().getLocation();
    }

    public GamePlayer getCarrier() {
        return this.carrier;
    }

    public Location getSource() {
        return this.source;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE)
                .append("flag", this.flag)
                .append("carrier", this.carrier)
                .build();
    }
}
