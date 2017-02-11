package pl.themolka.arcade.match;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GamePlayer;

/**
 * Fired when a {@link GamePlayer} leaves {@link Observers}.
 */
public class ObserversLeaveEvent extends ObserversEvent {
    public ObserversLeaveEvent(ArcadePlugin plugin, GamePlayer player, Observers observers) {
        super(plugin, player, observers);
    }
}
