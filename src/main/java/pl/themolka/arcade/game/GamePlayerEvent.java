package pl.themolka.arcade.game;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.session.PlayerEvent;

public class GamePlayerEvent extends PlayerEvent {
    private final GamePlayer player;

    public GamePlayerEvent(ArcadePlugin plugin, GamePlayer player) {
        super(plugin, player.getPlayer());

        this.player = player;
    }

    public GamePlayer getGamePlayer() {
        return this.player;
    }
}
