package pl.themolka.arcade.team;

import net.engio.mbassy.listener.Handler;
import pl.themolka.arcade.command.GameCommands;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.filter.FilterResult;
import pl.themolka.arcade.game.GameModule;

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
    public void onJoinCommand(GameCommands.JoinCommandEvent event) {
        if (event.isCanceled()) {
            return;
        }

        for (TeamLockRule rule : this.rules) {
            FilterResult result = rule.getFilter().filter(event.getJoinPlayer(), this.teams, this.teams.getMatch());
            if (result.equals(FilterResult.DENY)) {
                event.setCanceled(true);
                event.setJoined(false);

                String message = rule.getMessage();
                if (message != null) {
                    event.getJoinPlayer().sendError(message);
                } else {
                    event.getJoinPlayer().sendError("You can't join this game right now.");
                }
                break;
            }
        }
    }
}
