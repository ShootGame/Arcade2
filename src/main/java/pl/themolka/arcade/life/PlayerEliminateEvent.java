package pl.themolka.arcade.life;

import org.bukkit.Location;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.game.GameEvent;
import pl.themolka.arcade.game.GamePlayer;

public class PlayerEliminateEvent extends GameEvent implements Cancelable {
    private final GamePlayer player;
    private final Location deathLocation;
    private int remainingLives;

    public PlayerEliminateEvent(ArcadePlugin plugin, GamePlayer player, int remainingLives, Location deathLocation) {
        super(plugin);

        this.player = player;
        this.remainingLives = remainingLives;
        this.deathLocation = deathLocation;
    }

    @Override
    public boolean isCanceled() {
        return this.remainingLives > LivesGame.ZERO;
    }

    // You should be using the "setRemainingLives(...)" method.
    @Override
    @Deprecated
    public void setCanceled(boolean cancel) {
        // Try to give amount of lives equal to the
        // death (but negative) based on the ZERO.
        this.setRemainingLives(LivesGame.ZERO - LivesGame.DEATH_REVOKE);
    }

    public GamePlayer getPlayer() {
        return this.player;
    }

    public Location getDeathLocation() {
        return this.deathLocation;
    }

    public int getRemainingLives() {
        return this.remainingLives;
    }

    public void setRemainingLives(int remainingLives) {
        this.remainingLives = remainingLives;
    }
}
