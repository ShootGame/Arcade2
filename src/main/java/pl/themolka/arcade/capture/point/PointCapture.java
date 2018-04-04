package pl.themolka.arcade.capture.point;

import net.engio.mbassy.listener.Handler;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import pl.themolka.arcade.capture.CaptureGame;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.Filters;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.life.PlayerDeathEvent;
import pl.themolka.arcade.region.Region;
import pl.themolka.arcade.region.RegionFieldStrategy;
import pl.themolka.arcade.session.PlayerJoinEvent;
import pl.themolka.arcade.session.PlayerMoveEvent;
import pl.themolka.arcade.session.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;

public class PointCapture implements Listener {
    public static final Filter DEFAULT_FILTER = Filters.undefined();
    public static final RegionFieldStrategy DEFAULT_FIELD_STRATEGY = RegionFieldStrategy.EXACT;

    private final CaptureGame game;
    private final Point point;

    private RegionFieldStrategy fieldStrategy = DEFAULT_FIELD_STRATEGY;
    private Filter filter = DEFAULT_FILTER;
    private final Set<GamePlayer> players = new HashSet<>();
    private Region region;

    public PointCapture(CaptureGame game, Point point) {
        this.game = game;
        this.point = point;
    }

    public boolean canCapture(Participator competitor) {
        return competitor != null && this.filter.filter(competitor).isNotFalse();
    }

    public void clearPlayers() {
        for (GamePlayer player : this.players) {
            this.game.getPlugin().getEventBus().publish(new PointPlayerLeaveEvent(this.game.getPlugin(), this, player));
        }

        this.players.clear();
    }

    public RegionFieldStrategy getFieldStrategy() {
        return this.fieldStrategy;
    }

    public Filter getFilter() {
        return this.filter;
    }

    public Set<GamePlayer> getPlayers() {
        return new HashSet<>(this.players);
    }

    public Point getPoint() {
        return this.point;
    }

    public Region getRegion() {
        return this.region;
    }

    public boolean hasPlayer(GamePlayer player) {
        return player != null && this.players.contains(player);
    }

    public boolean playerEnter(GamePlayer player) {
        if (player != null && !this.hasPlayer(player)) {
            this.game.getPlugin().getEventBus().publish(new PointPlayerEnterEvent(this.game.getPlugin(), this, player));
            return this.players.add(player);
        }

        return false;
    }

    public boolean playerLeave(GamePlayer player) {
        if (player != null && this.hasPlayer(player)) {
            this.game.getPlugin().getEventBus().publish(new PointPlayerLeaveEvent(this.game.getPlugin(), this, player));
            return players.remove(player);
        }

        return false;
    }

    public void setFieldStrategy(RegionFieldStrategy fieldStrategy) {
        this.fieldStrategy = fieldStrategy;
    }

    public void setFilter(Filter filter) {
        this.filter = filter != null ? filter : DEFAULT_FILTER;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, Point.TO_STRING_STYLE)
                .append("fieldStrategy", this.fieldStrategy)
                .append("filter", this.filter)
                .append("region", this.region)
                .toString();
    }

    //
    // Tracking Players
    //

    @Handler(priority = Priority.LAST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        this.playerLeave(event.getVictim());
    }

    // We are unable to use PlayerInitialSpawnEvent because our player sessions
    // are created or restored in the PlayerJoinEvent which is called after it.
    @Handler(priority = Priority.LAST)
    public void onPlayerInitialSpawn(PlayerJoinEvent event) {
        GamePlayer player = event.getGamePlayer();
        if (this.shouldTrack(player, event.getBukkitPlayer().getLocation())) {
            this.playerEnter(player);
        }
    }

    @Handler(priority = Priority.LAST)
    public void onPlayerMove(PlayerMoveEvent event) {
        this.onPlayerMove(event.getGamePlayer(), event.getTo());
    }

    @Handler(priority = Priority.LAST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.playerLeave(event.getGamePlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        this.onPlayerMove(this.convertPlayer(event.getPlayer()), event.getTo());
    }

    private GamePlayer convertPlayer(Player bukkit) {
        return bukkit != null ? this.game.getGame().getPlayer(bukkit) : null;
    }

    private void onPlayerMove(GamePlayer player, Location to) {
        if (player != null) {
            if (this.shouldTrack(player, to)) {
                this.playerEnter(player);
            } else {
                this.playerLeave(player);
            }
        }
    }

    private boolean shouldTrack(GamePlayer player, Location at) {
        if (player == null || at == null || player.isDead()) {
            return false;
        }

        return this.getFieldStrategy().regionContains(this.getRegion(), at);
    }
}
