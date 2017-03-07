package pl.themolka.arcade.score;

import net.engio.mbassy.listener.Handler;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.match.DrawMatchWinner;
import pl.themolka.arcade.match.DynamicWinnable;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.match.MatchGame;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.match.MatchWinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreGame extends GameModule implements DynamicWinnable {
    private final int kills;
    private final int limit;
    private final String name;

    private Match match;

    public ScoreGame(int kills, int limit, String name) {
        this.kills = kills;
        this.limit = limit;
        this.name = name;
    }

    @Override
    public void onEnable() {
        GameModule module = this.getGame().getModule(MatchModule.class);
        if (module != null) {
            this.match = ((MatchGame) module).getMatch();
        }

        for (MatchWinner winner : this.getMatch().getWinnerList()) {
            Score score = new Score(this, winner);
            score.setLimit(this.getLimit());
            score.setName(this.getName());

            if (this.getScore(winner) == null) {
                winner.addGoal(score);
            }
        }
    }

    @Override
    public DrawMatchWinner getDrawWinner() {
        return this.getMatch().getDrawWinner();
    }

    @Override
    public List<MatchWinner> getDynamicWinners() {
        int highestScore = 0;

        List<MatchWinner> results = new ArrayList<>();
        for (MatchWinner winner : this.getMatch().getWinners()) {
            if (winner.areGoalsCompleted()) {
                return Collections.singletonList(winner);
            }

            Score score = this.getScore(winner);
            if (score == null) {
                continue;
            } else if (score.getScore() > highestScore) {
                results.clear();
                highestScore = score.getScore();
            }

            if (score.getScore() >= highestScore) {
                GoalHolder owner = score.getOwner();
                if (owner instanceof MatchWinner) {
                    results.add((MatchWinner) owner);
                }
            }
        }

        if (highestScore != 0 || results.isEmpty()) {
            return results;
        }

        return null;
    }

    public int getKills() {
        return this.kills;
    }

    public int getLimit() {
        return this.limit;
    }

    public Match getMatch() {
        return this.match;
    }

    public String getName() {
        return this.name;
    }

    public Score getScore(MatchWinner winner) {
        for (Goal goal : winner.getGoals()) {
            if (goal instanceof Score) {
                return (Score) goal;
            }
        }

        return null;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (this.getMatch() == null || event.getEntity().getKiller() == null) {
            return;
        }

        MatchWinner winner = this.getMatch().findWinnerByPlayer(event.getEntity().getKiller());
        if (winner == null) {
            return;
        }

        Score score = this.getScore(winner);
        if (score != null) {
            score.incrementScore(this.getKills());
        }
    }

    @Handler(priority = Priority.HIGHER)
    public void onScoreLimitReach(ScoreLimitReachEvent event) {
        this.getMatch().sendGoalMessage("The score limit of " + event.getLimit() + " points for " +
                event.getScore().getOwner().getTitle() + ChatColor.RESET + ChatColor.YELLOW + " has been reached.");
    }
}
