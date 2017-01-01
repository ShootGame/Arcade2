package pl.themolka.arcade.team;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GamePlayer;

public class PlayerLeftTeamEvent extends PlayerTeamEvent {
    public PlayerLeftTeamEvent(ArcadePlugin plugin, GamePlayer player, Team team) {
        super(plugin, player, team);
    }
}
