package pl.themolka.arcade.team;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.GamePlayerEvent;

public class PlayerTeamEvent extends GamePlayerEvent {
    private final Team team;

    public PlayerTeamEvent(ArcadePlugin plugin, GamePlayer player, Team team) {
        super(plugin, player);

        this.team = team;
    }

    public Team getTeam() {
        return this.team;
    }
}
