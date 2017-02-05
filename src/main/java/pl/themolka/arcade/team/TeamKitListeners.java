package pl.themolka.arcade.team;

import net.engio.mbassy.listener.Handler;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.kit.Kit;
import pl.themolka.arcade.kit.KitsGame;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.match.MatchStartedEvent;

public class TeamKitListeners {
    private final TeamsGame game;
    private final KitsGame kits;
    private final Match match;

    public TeamKitListeners(TeamsGame game, KitsGame kits, Match match) {
        this.game = game;
        this.kits = kits;
        this.match = match;
    }

    public Kit findKit(String id) {
        return this.kits.getKit(id);
    }

    @Handler(priority = Priority.HIGHEST)
    public void onClearingOldInventory(MatchStartedEvent event) {
        for (Team team : this.game.getTeams()) {
            if (team.isParticipating()) {
                for (GamePlayer member : team.getOnlineMembers()) {
                    member.getPlayer().clearInventory(true);
                }
            }
        }
    }

    @Handler(priority = Priority.HIGHER)
    public void onPlayerJoinedTeam(PlayerJoinedTeamEvent event) {
        if (!this.match.isRunning()) {
            return;
        }


    }

    @Handler(priority = Priority.HIGHER)
    public void onPlayerLeftTeam(PlayerLeftTeamEvent event) {
        if (event.getTeam().isObserving()) {
            return;
        }

        // TODO clear
    }
}
