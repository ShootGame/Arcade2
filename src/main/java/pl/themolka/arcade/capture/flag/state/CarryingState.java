package pl.themolka.arcade.capture.flag.state;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import pl.themolka.arcade.capture.flag.Flag;
import pl.themolka.arcade.capture.flag.FlagDroppedEvent;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.time.Time;

public class CarryingState extends FlagState.Permanent implements FlagState.PhysicalFlag {
    private final GamePlayer carrier;
    private final Location source;

    private ItemStack carrierRealHelmet;
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
    public Location getLocation() {
        return this.carrier.getBukkit().getLocation();
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

            this.detachBannerFromPlayer(this.carrier);

            this.game.getMatch().sendGoalMessage(this.getDropMessage(owner));
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

    public void attachBannerToPlayer(GamePlayer player) {
        PlayerInventory inventory = player.getBukkit().getInventory();
        this.carrierRealHelmet = inventory.getHelmet().clone();

        inventory.setHelmet(this.flag.getItem().clone());
    }

    public void detachBannerFromPlayer(GamePlayer player) {
        this.flag.removeFlagItems(player);
        player.getBukkit().getInventory().setHelmet(this.carrierRealHelmet);
    }

    public GamePlayer getCarrier() {
        return this.carrier;
    }

    public String getDropMessage(GoalHolder ownerCompetitor) {
        String owner = "";
        if (ownerCompetitor != null) {
            owner = ChatColor.GOLD + ownerCompetitor.getTitle() + ChatColor.YELLOW + "'s ";
        }

        return ChatColor.GOLD.toString() + ChatColor.BOLD + this.carrier.getDisplayName() +
                ChatColor.RESET + ChatColor.YELLOW + " has dropped " + owner + this.flag.getColoredName();
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
