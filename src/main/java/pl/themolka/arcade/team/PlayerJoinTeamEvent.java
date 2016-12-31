package pl.themolka.arcade.team;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.commons.event.Cancelable;

public class PlayerJoinTeamEvent extends PlayerTeamEvent implements Cancelable {
    private boolean cancel;

    public PlayerJoinTeamEvent(ArcadePlugin plugin, GamePlayer player, Team team) {
        super(plugin, player, team);
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }
}
