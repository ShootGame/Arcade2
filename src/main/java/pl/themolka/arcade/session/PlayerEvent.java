package pl.themolka.arcade.session;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Event;
import pl.themolka.arcade.game.GamePlayer;

public class PlayerEvent extends Event {
    private final ArcadePlayer player;

    public PlayerEvent(ArcadePlugin plugin, ArcadePlayer player) {
        super(plugin);

        this.player = player;
    }

    public GamePlayer getGamePlayer() {
        return this.getPlayer().getGamePlayer();
    }

    public ArcadePlayer getPlayer() {
        return this.player;
    }
}
