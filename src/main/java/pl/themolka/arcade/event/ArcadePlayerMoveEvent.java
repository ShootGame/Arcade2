package pl.themolka.arcade.event;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerMoveEvent;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.GamePlayerEvent;

public class ArcadePlayerMoveEvent extends GamePlayerEvent implements Cancelable {
    private boolean cancel;
    private final Location from;
    private final Location to;

    public ArcadePlayerMoveEvent(ArcadePlugin plugin, GamePlayer player, PlayerMoveEvent event) {
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
