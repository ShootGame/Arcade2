package pl.themolka.arcade.region.tracker;

import net.engio.mbassy.listener.Handler;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;
import pl.themolka.arcade.event.EventListenerComponent;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.PlayerResolver;
import pl.themolka.arcade.life.PlayerDeathEvent;
import pl.themolka.arcade.session.PlayerJoinEvent;
import pl.themolka.arcade.session.PlayerMoveEvent;
import pl.themolka.arcade.session.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PlayerTracker implements EventListenerComponent, PlayerTrackerFilter, PlayerTrackerListener {
    private final PlayerResolver playerResolver;
    private final PlayerTrackerFilter filter;
    private final PlayerTrackerListener listener;

    /** Currently tracked players */
    private final Set<GamePlayer> tracking = new HashSet<>();

    public PlayerTracker(PlayerResolver playerResolver) {
        this(playerResolver, null, null);
    }

    public PlayerTracker(PlayerResolver playerResolver, PlayerTrackerFilter filter) {
        this(playerResolver, filter, null);
    }

    public PlayerTracker(PlayerResolver playerResolver, PlayerTrackerListener listener) {
        this(playerResolver, null, listener);
    }

    public PlayerTracker(PlayerResolver playerResolver, PlayerTrackerFilter filter, PlayerTrackerListener listener) {
        this.playerResolver = Objects.requireNonNull(playerResolver, "playerResolver cannot be null");
        this.filter = filter;
        this.listener = listener;
    }

    @Override
    public boolean canTrack(GamePlayer player, Location at) {
        return this.filter == null || this.filter.canTrack(player, at);
    }

    @Override
    public void onEnter(GamePlayer player, Location enter) {
        if (this.listener != null) {
            this.listener.onEnter(player, enter);
        }
    }

    @Override
    public void onLeave(GamePlayer player, Location leave) {
        if (this.listener != null) {
            this.listener.onLeave(player, leave);
        }
    }

    public boolean contains(GamePlayer player) {
        return player != null && this.tracking.contains(player);
    }

    public PlayerTrackerListener getListener() {
        return this.listener;
    }

    public Set<GamePlayer> getTracking() {
        return new HashSet<>(this.tracking);
    }

    //
    // Listeners
    //

    protected boolean enter(GamePlayer player, Location enter) {
        if (this.tracking.add(player)) {
            this.onEnter(player, enter);
            return true;
        }

        return false;
    }

    protected boolean leave(GamePlayer player, Location leave) {
        if (this.tracking.remove(player)) {
            this.onLeave(player, leave);
            return true;
        }

        return false;
    }

    @Handler(priority = Priority.LAST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        this.leave(event.getVictim(), event.getVictimBukkit().getLocation());
    }

    // We are unable to use PlayerInitialSpawnEvent because our player sessions
    // are created or restored in the PlayerJoinEvent which is called after it.
    @Handler(priority = Priority.LAST)
    public void onPlayerInitialSpawn(PlayerJoinEvent event) {
        GamePlayer player = event.getGamePlayer();
        Location location = event.getBukkitPlayer().getLocation();
        if (this.shouldTrack(player, location)) {
            this.enter(player, location);
        }
    }

    @Handler(priority = Priority.LAST)
    public void onPlayerMove(PlayerMoveEvent event) {
        this.onPlayerMove(event.getGamePlayer(), event.getTo());
    }

    @Handler(priority = Priority.LAST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.leave(event.getGamePlayer(), event.getBukkitPlayer().getLocation());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        this.onPlayerMove(this.playerResolver.resolve(event.getPlayer()), event.getTo());
    }

    private void onPlayerMove(GamePlayer player, Location to) {
        if (player != null) {
            if (this.shouldTrack(player, to)) {
                this.enter(player, to);
            } else {
                this.leave(player, to);
            }
        }
    }

    private boolean shouldTrack(GamePlayer player, Location at) {
        return player != null && at != null && !player.isDead() && this.canTrack(player, at);
    }
}
