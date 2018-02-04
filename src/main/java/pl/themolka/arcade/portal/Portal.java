package pl.themolka.arcade.portal;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.Filters;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.PlayerApplicable;
import pl.themolka.arcade.game.PlayerTrigger;
import pl.themolka.arcade.region.Region;

import java.util.Random;

public class Portal implements PlayerApplicable {
    public static final PlayerTeleportEvent.TeleportCause TELEPORT_CAUSE =
            PlayerTeleportEvent.TeleportCause.NETHER_PORTAL;
    public static final Sound TELEPORT_SOUND = Sound.ENTITY_ENDERMEN_TELEPORT;

    private final Random random = new Random();

    private final ArcadePlugin plugin;

    private Filter filter = Filters.undefined();
    private Region destination;
    private Region region;
    private final PlayerTrigger trigger = PlayerTrigger.create();

    public Portal(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    public Portal(PortalsGame game) {
        this(game.getPlugin());
    }

    @Override
    public void apply(GamePlayer player) {
        Player bukkit = player.getBukkit();
        if (bukkit != null) {
            this.teleport(player, bukkit.getLocation());
        }
    }

    public boolean canTeleport(GamePlayer player) {
        return player != null && player.isOnline() && this.filter.filter(player).isNotDenied();
    }

    public Filter getFilter() {
        return this.filter;
    }

    public Region getDestination() {
        return this.destination;
    }

    public Region getRegion() {
        return this.region;
    }

    public PlayerTrigger getTrigger() {
        return this.trigger;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public void setDestination(Region destination) {
        this.destination = destination;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public void teleport(GamePlayer player, Location source) {
        if (!this.canTeleport(player)) {
            return;
        }

        Vector vector = this.destination.getRandomVector(this.random, Region.RANDOM_LIMIT);
        if (vector == null) {
            return;
        }

        World world = this.destination.getWorld();
        float yaw = source.getYaw();
        float pitch = source.getPitch();
        Location to = vector.toLocation(world, yaw, pitch);

        PlayerPortalEvent event = new PlayerPortalEvent(this.plugin, this, player, to, TELEPORT_SOUND);
        this.plugin.getEventBus().publish(event);

        Location target = event.getDestination();
        if (target != null && !event.isCanceled()) {
            this.teleport(player, target, event.getSound());
        }
    }

    private void teleport(GamePlayer player, Location destination, Sound sound) {
        Player bukkit = player.getBukkit();

        bukkit.teleport(destination, TELEPORT_CAUSE);
        bukkit.setFallDistance(0.0F);

        if (sound != null) {
            bukkit.playSound(destination, sound, 1F, 1F);
        }

        this.trigger.apply(player);
    }
}
