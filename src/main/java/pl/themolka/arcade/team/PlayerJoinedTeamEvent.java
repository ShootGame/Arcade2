package pl.themolka.arcade.team;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GamePlayer;

public class PlayerJoinedTeamEvent extends PlayerTeamEvent {
    public PlayerJoinedTeamEvent(ArcadePlugin plugin, GamePlayer player, Team team) {
        super(plugin, player, team);
    }
}
