package pl.themolka.arcade.portal;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.Filters;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.PlayerApplicable;
import pl.themolka.arcade.kit.Kit;
import pl.themolka.arcade.region.ForwardingRegion;
import pl.themolka.arcade.region.Region;
import pl.themolka.arcade.region.RegionFieldStrategy;
import pl.themolka.arcade.spawn.SpawnApply;

public class Portal extends ForwardingRegion implements PlayerApplicable {
    public static final RegionFieldStrategy FIELD_STRATEGY = RegionFieldStrategy.NET;
    public static final Sound TELEPORT_SOUND = Sound.ENTITY_ENDERMEN_TELEPORT;

    private final ArcadePlugin plugin;

    private Filter filter = Filters.undefined();
    private SpawnApply destination;
    private Kit kit;
    private Region region;

    public Portal(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    public Portal(PortalsGame game) {
        this(game.getPlugin());
    }

    @Override
    public void apply(GamePlayer player) {
        Player bukkit = player.getBukkit();
        if (bukkit != null && this.canTeleport(player)) {
            this.teleport(player);
        }
    }

    @Override
    protected Region delegate() {
        return this.region;
    }

    public boolean canTeleport(GamePlayer player) {
        if (player != null && player.isOnline() && !player.isDead()) {
            return !player.isParticipating() || this.filter.filter(player).isNotDenied();
        }

        return false;
    }

    public Filter getFilter() {
        return this.filter;
    }

    public SpawnApply getDestination() {
        return this.destination;
    }

    public RegionFieldStrategy getFieldStrategy() {
        return FIELD_STRATEGY;
    }

    public Kit getKit() {
        return this.kit;
    }

    public boolean hasKit() {
        return this.kit != null;
    }

    public void setFilter(Filter filter) {
        this.filter = filter != null ? filter : Filters.undefined();
    }

    public void setDestination(SpawnApply destination) {
        this.destination = destination;
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Location teleport(GamePlayer player) {
        PlayerPortalEvent event = new PlayerPortalEvent(this.plugin, this, player, this.destination, TELEPORT_SOUND);
        this.plugin.getEventBus().publish(event);

        SpawnApply destination = event.getDestination();
        if (destination == null || event.isCanceled()) {
            return null;
        }

        Location location = destination.spawn(player);

        if (location != null) {
            this.playSound(location, event.getSound(), player);

            if (this.hasKit()) {
                this.kit.apply(player);
            }
        }

        return location;
    }

    private void playSound(Location location, Sound sound, GamePlayer player) {
        if (location != null && sound != null) {
            player.getBukkit().playSound(location, sound, 1F, 1F);
        }
    }
}
