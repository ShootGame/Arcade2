package pl.themolka.arcade.life;

import net.engio.mbassy.listener.Handler;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.match.DynamicWinnable;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.match.MatchGame;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.match.MatchWinner;
import pl.themolka.arcade.team.PlayerJoinedTeamEvent;
import pl.themolka.arcade.team.PlayerLeftTeamEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KillEnemiesGame extends GameModule implements DynamicWinnable {
    private final Map<GoalHolder, KillEnemies> byOwner = new HashMap<>();

    private Match match;

    @Override
    public void onEnable() {
        GameModule module = this.getGame().getModule(MatchModule.class);
        if (module == null || !(module instanceof MatchGame)) {
            return;
        }

        this.match = ((MatchGame) module).getMatch();
        this.getMatch().registerDynamicWinnable(this);

        Collection<MatchWinner> winnerList = this.getMatch().getWinnerList();
        for (MatchWinner winner : winnerList) {
            KillEnemies objective = new KillEnemies(this, winner, this.fetchEnemies(winnerList, winner));

            // Make sure that KillEnemies is only ONE per GoalHolder
            if (objective.isCompletableBy(winner) && this.getObjective(winner) == null) {
                this.byOwner.put(winner, objective);
                winner.addGoal(objective);
            }
        }
    }

    @Override
    public List<MatchWinner> getDynamicWinners() {
        double highestProgress = Goal.PROGRESS_UNTOUCHED;

        List<MatchWinner> results = new ArrayList<>();
        for (MatchWinner winner : this.getMatch().getWinnerList()) {
            if (winner.areGoalsCompleted()) {
                results.add(winner);
                continue;
            }

            KillEnemies objective = this.getObjective(winner);
            if (objective == null) {
                continue;
            }

            double progress = objective.getProgress();
            if (progress > highestProgress) {
                results.clear();
                highestProgress = progress;
            }

            if (!results.contains(winner) && progress >= highestProgress) {
                results.add(winner);
            }
        }

        if (highestProgress != Goal.PROGRESS_UNTOUCHED && !results.isEmpty()) {
            return results;
        }

        return null;
    }

    public Match getMatch() {
        return this.match;
    }

    public KillEnemies getObjective(GoalHolder holder) {
        return this.byOwner.get(holder);
    }

    public Collection<KillEnemies> getObjectives() {
        return this.byOwner.values();
    }

    private Set<GoalHolder> fetchEnemies(Collection<MatchWinner> all, MatchWinner owner) {
        Set<GoalHolder> enemies = new HashSet<>();
        for (GoalHolder competitor : all) {
            if (!competitor.equals(owner)) {
                enemies.add(competitor);
            }
        }

        return enemies;
    }

    //
    // Refreshing Objectives
    //

    public void refreshObjectives() {
        for (KillEnemies objective : this.byOwner.values()) {
            objective.refreshCompletion();
        }
    }

    @Handler(priority = Priority.LOWEST)
    public void teamJoined(PlayerJoinedTeamEvent event) {
        if (event.getTeam().isParticipating()) {
            this.refreshObjectives();
        }
    }

    @Handler(priority = Priority.LOWEST)
    public void teamLeft(PlayerLeftTeamEvent event) {
        if (event.getTeam().isParticipating()) {
            this.refreshObjectives();
        }
    }
}
