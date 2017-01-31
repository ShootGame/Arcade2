package pl.themolka.arcade.lobby;

import net.engio.mbassy.listener.Handler;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.session.PlayerQuitEvent;

public class LobbyGame extends GameModule {
    /**
     * We need to remove GamePlayers from the Game due to possible memory leaks.
     */
    @Handler(priority = Priority.LAST)
    public void removeGamePlayer(PlayerQuitEvent event) {
        this.getGame().removePlayer(event.getGamePlayer());
    }
}
