package pl.themolka.arcade.lobby;

import net.engio.mbassy.listener.Handler;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.IGameModuleConfig;
import pl.themolka.arcade.session.PlayerQuitEvent;

public class LobbyGame extends GameModule {
    protected LobbyGame() {
    }

    /**
     * We need to remove GamePlayers from the Game due to possible memory leaks.
     */
    @Handler(priority = Priority.LAST)
    public void removeGamePlayer(PlayerQuitEvent event) {
        this.getGame().getPlayers().unregister(event.getGamePlayer());
    }

    public interface Config extends IGameModuleConfig<LobbyGame> {
        @Override
        default LobbyGame create(Game game) {
            return new LobbyGame();
        }
    }
}
