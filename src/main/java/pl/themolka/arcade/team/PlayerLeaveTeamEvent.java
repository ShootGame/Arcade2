package pl.themolka.arcade.team;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.game.GamePlayer;

public class PlayerLeaveTeamEvent extends PlayerTeamEvent implements Cancelable {
    private boolean cancel;

    public PlayerLeaveTeamEvent(ArcadePlugin plugin, GamePlayer player, Team team) {
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
