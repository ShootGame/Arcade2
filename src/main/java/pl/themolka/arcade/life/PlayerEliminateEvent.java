package pl.themolka.arcade.life;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GameEvent;
import pl.themolka.arcade.game.GamePlayer;

public class PlayerEliminateEvent extends GameEvent {
    private final GamePlayer player;

    public PlayerEliminateEvent(ArcadePlugin plugin, GamePlayer player) {
        super(plugin);

        this.player = player;
    }

    public GamePlayer getPlayer() {
        return this.player;
    }
}
