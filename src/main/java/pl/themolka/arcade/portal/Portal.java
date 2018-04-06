package pl.themolka.arcade.portal;

import org.bukkit.Location;
import org.bukkit.Sound;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.config.Unique;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.Filters;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.PlayerApplicable;
import pl.themolka.arcade.kit.Kit;
import pl.themolka.arcade.region.AbstractRegion;
import pl.themolka.arcade.region.ForwardingRegion;
import pl.themolka.arcade.region.Region;
import pl.themolka.arcade.region.RegionFieldStrategy;
import pl.themolka.arcade.spawn.SpawnApply;
import pl.themolka.arcade.util.StringId;

public class Portal extends ForwardingRegion implements PlayerApplicable, StringId {
    public static final RegionFieldStrategy FIELD_STRATEGY = RegionFieldStrategy.NET;
    public static final Sound TELEPORT_SOUND = Sound.ENTITY_ENDERMEN_TELEPORT;

    private final SpawnApply destination;
    private final Filter filter;
    private final String id;
    private final Kit kit;
    private final Region region;

    protected Portal(Game game, IGameConfig.Library library, Config config) {
        this.destination = config.destination();
        this.filter = Filters.secure(config.filter().getIfPresent());
        this.id = config.id();
        this.kit = config.kit().getIfPresent();
        this.region = library.getOrDefine(game, config.region().get());
    }

    @Override
    public void apply(GamePlayer player) {
        if (this.canTeleport(player)) {
            this.teleport(player);
        }
    }

    @Override
    protected Region delegate() {
        return this.region;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public boolean canTeleport(GamePlayer player) {
        if (player != null && player.isOnline() && !player.isDead()) {
            return !player.isParticipating() || this.filter.filter(player).isNotFalse();
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

    public Location teleport(GamePlayer player) {
        if (player == null || !player.isOnline()) {
            return null;
        }

        PlayerPortalEvent event = new PlayerPortalEvent(this.getPlugin(), this, player, this.destination, TELEPORT_SOUND);
        this.getPlugin().getEventBus().publish(event);

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

    public interface Config extends IGameConfig<Portal>, Unique {
        SpawnApply destination();
        default Ref<Filter> filter() { return Ref.empty(); }
        default Ref<Kit> kit() { return Ref.empty(); }
        Ref<AbstractRegion.Config<?>> region();

        @Override
        default Portal create(Game game, Library library) {
            return new Portal(game, library, this);
        }
    }
}
