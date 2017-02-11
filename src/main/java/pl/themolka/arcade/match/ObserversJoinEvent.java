package pl.themolka.arcade.match;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GamePlayer;

/**
 * Fired when a {@link GamePlayer} joins {@link Observers}.
 */
public class ObserversJoinEvent extends ObserversEvent {
    public ObserversJoinEvent(ArcadePlugin plugin, GamePlayer player, Observers observers) {
        super(plugin, player, observers);
    }
}
