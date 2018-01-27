package pl.themolka.arcade.team;

import net.engio.mbassy.listener.Handler;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.filter.FilterResult;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GamePlayer;

import java.util.ArrayList;
import java.util.List;

public class TeamLockGame extends GameModule {
    private final List<TeamLockRule> rules = new ArrayList<>();
    private final TeamsGame teams;

    public TeamLockGame(List<TeamLockRule> rules, TeamsGame teams) {
        this.rules.addAll(rules);
        this.teams = teams;
    }

    @Handler(priority = Priority.HIGHER)
    public void onPlayerJoinTeam(PlayerJoinTeamEvent event) {
        if (event.isCanceled() || event.getTeam().isObservers()) {
            return;
        }

        GamePlayer player = event.getGamePlayer();
        for (TeamLockRule rule : this.rules) {
            FilterResult result = rule.getFilter().filter(event.getPlayer(), player, this.teams, this.teams.getMatch());
            if (result.equals(FilterResult.DENY)) {
                event.setCanceled(true);

                String message = rule.getMessage();
                if (message != null) {
                    player.sendError(message);
                } else {
                    player.sendError("You can't join this game right now.");
                }
                break;
            }
        }
    }
}
