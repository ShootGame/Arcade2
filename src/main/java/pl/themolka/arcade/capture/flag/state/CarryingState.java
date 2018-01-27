package pl.themolka.arcade.capture.flag.state;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import pl.themolka.arcade.capture.flag.Flag;
import pl.themolka.arcade.capture.flag.FlagDroppedEvent;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.time.Time;

public class CarryingState extends FlagState.Permanent implements FlagState.VirtualFlag {
    private final GamePlayer carrier;
    private final Location source;

    private Location lastSecureLocation;

    public CarryingState(Flag flag, GamePlayer carrier, Location source) {
        super(flag);

        this.carrier = carrier;
        this.source = source;

        this.lastSecureLocation = source.clone();
    }

    @Override
    public FlagState copy() {
        return new CarryingState(this.flag, this.carrier, this.source);
    }

    @Override
    public void heartbeat(long ticks, Match match, GoalHolder owner) {
        if (this.carrier.isOnline() && !this.carrier.isDead() && this.carrier.isParticipating()) {
            this.heartbeatCarrier(ticks, match, owner);

            Location location = this.carrier.getBukkit().getLocation();
            if (this.isLocationSecure(location)) {
                this.setLastSecureLocation(location);
            }
        } else {
            DroppedState droppedState = this.flag.createDroppedState(this.carrier, this.lastSecureLocation);

            FlagDroppedEvent event = new FlagDroppedEvent(this.game.getPlugin(), this.flag, this, droppedState, this.carrier);
            this.game.getPlugin().getEventBus().publish(event);

            if (event.isCanceled()) {
                return;
            }

            this.game.getMatch().sendGoalMessage(this.flag.getDropMessage(this.carrier));
            this.flag.setState(event.getNewState());
        }
    }

    public void heartbeatCarrier(long ticks, Match match, GoalHolder owner) {
        // This message will blink, it would be strange if it would blink
        // every tick. Send this message just every real second then.
        if (ticks % Time.SECOND.toTicks() == 0) {
            this.carrier.sendAction(ChatColor.WHITE.toString() + ChatColor.BOLD + "You are carrying " +
                    this.flag.getColoredName() + ChatColor.RESET + ChatColor.WHITE + ChatColor.BOLD + ".");
        }
    }

    public GamePlayer getCarrier() {
        return this.carrier;
    }

    public Location getSource() {
        return this.source;
    }

    public Location getLastSecureLocation() {
        return this.lastSecureLocation;
    }

    public boolean isLocationSecure(Location location) {
        return true;
    }

    public void setLastSecureLocation(Location lastSecureLocation) {
        this.lastSecureLocation = lastSecureLocation;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE)
                .append("flag", this.flag)
                .append("carrier", this.carrier)
                .append("source", this.source)
                .build();
    }
}
