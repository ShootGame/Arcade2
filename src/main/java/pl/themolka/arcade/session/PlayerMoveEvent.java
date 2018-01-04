package pl.themolka.arcade.session;

import org.bukkit.Location;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.GamePlayerEvent;

public class PlayerMoveEvent extends GamePlayerEvent implements Cancelable {
    private boolean cancel;
    private final Location from;
    private final Location to;

    public PlayerMoveEvent(ArcadePlugin plugin, GamePlayer player,
                           org.bukkit.event.player.PlayerMoveEvent event) {
        super(plugin, player);

        this.from = event.getFrom();
        this.to = event.getTo();
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }

    public Location getFrom() {
        return this.from;
    }

    public Location getTo() {
        return this.to;
    }
}
