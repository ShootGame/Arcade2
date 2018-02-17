package pl.themolka.arcade.score;

import org.bukkit.Location;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.portal.Portal;

/**
 * Representation of a single Score Box which is used to score given amount of
 * points for the {@link pl.themolka.arcade.game.Participator} and respawn the
 * scoring player. We can use portals for all of these executions.
 */
public class ScoreBox extends Portal {
    public static final double POINTS = 1.0D;

    private final double points;

    public ScoreBox(ArcadePlugin plugin, double points) {
        super(plugin);

        this.points = points;
    }

    // Use canScore(...) instead.
    @Override
    @Deprecated
    public boolean canTeleport(GamePlayer player) {
        return this.canScore(player);
    }

    /*
     * Same as in portals, but observers and such are not allowed
     * to be teleported since they cannot score points.
     */
    public boolean canScore(GamePlayer player) {
        return player.isParticipating() && super.canTeleport(player);
    }

    public double getPoints() {
        return this.points;
    }

    public Location score(Score score, GamePlayer player) {
        if (score == null || player == null || !player.isOnline()) {
            return null;
        }

        ScoreBoxEvent event = new ScoreBoxEvent(this.plugin, score, this, player, this.points);
        this.plugin.getEventBus().publish(event);

        double points = event.getPoints();
        if (event.isCanceled() || !Score.isValid(points)) {
            return null;
        }

        score.incrementScore(player, points);
        return this.teleport(player);
    }
}
