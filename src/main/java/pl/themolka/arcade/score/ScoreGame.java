package pl.themolka.arcade.score;

import net.engio.mbassy.listener.Handler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.match.DynamicWinnable;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.match.MatchGame;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.match.MatchWinner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreGame extends GameModule implements DynamicWinnable {
    private final Map<GoalHolder, Score> byOwner = new HashMap<>();

    private final double limit;
    private final String scoreName;

    private double deathLoss;
    private double killReward;

    private Match match;

    public ScoreGame(double limit, String scoreName) {
        this.limit = limit;
        this.scoreName = scoreName;
    }

    @Override
    public void onEnable() {
        GameModule module = this.getGame().getModule(MatchModule.class);
        if (module == null || !(module instanceof MatchGame)) {
            return;
        }

        this.match = ((MatchGame) module).getMatch();
        this.getMatch().registerDynamicWinnable(this);

        for (MatchWinner completableBy : this.getMatch().getWinnerList()) {
            Score score = new Score(this, completableBy);
            score.setLimit(this.getLimit());
            score.setName(this.getScoreName());

            // Make sure that Score is only ONE per GoalHolder
            if (score.isCompletableBy(completableBy) && this.getScore(completableBy) == null) {
                this.byOwner.put(completableBy, score);
                completableBy.addGoal(score);
            }
        }
    }

    @Override
    public List<MatchWinner> getDynamicWinners() {
        double highestScore = Score.MIN;

        List<MatchWinner> results = new ArrayList<>();
        for (MatchWinner winner : this.getMatch().getWinnerList()) {
            if (winner.areGoalsCompleted()) {
                results.add(winner);
            }

            Score score = this.getScore(winner);
            if (score == null) {
                continue;
            } else if (score.getScore() > highestScore) {
                results.clear();
                highestScore = score.getScore();
            }

            if (!results.contains(winner) && score.getScore() >= highestScore) {
                results.add(winner);
            }
        }

        if (highestScore != Score.MIN && !results.isEmpty()) {
            return results;
        }

        return null;
    }

    public Collection<Score> getAll() {
        return this.byOwner.values();
    }

    public double getDeathLoss() {
        return this.deathLoss;
    }

    public double getKillReward() {
        return this.killReward;
    }

    public double getLimit() {
        return this.limit;
    }

    public Match getMatch() {
        return this.match;
    }

    public Score getScore(GoalHolder holder) {
        return this.byOwner.get(holder);
    }

    public String getScoreName() {
        return this.scoreName;
    }

    public void setDeathLoss(double deathLoss) {
        this.deathLoss = deathLoss;
    }

    public void setKillReward(double killReward) {
        this.killReward = killReward;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Match match = this.getMatch();

        if (match != null) {
            Player victim = event.getEntity();
            Player killer = victim.getKiller();

            // kill registration
            boolean wasKill = this.registerPoints(match, killer, this.getKillReward());

            // death registration
            if (!wasKill) {
                this.registerPoints(match, victim, -this.getDeathLoss());
            }
        }
    }

    @Handler(priority = Priority.HIGHER)
    public void onScoreLimitReach(ScoreLimitReachEvent event) {
        this.getMatch().sendGoalMessage(ChatColor.YELLOW + "The score limit of " + ChatColor.GOLD + ChatColor.BOLD +
                Math.round(event.getLimit()) + " points" + ChatColor.RESET + ChatColor.YELLOW + " for " + ChatColor.GOLD +
                event.getScore().getOwner().getTitle() + ChatColor.YELLOW + " has been reached.");
    }

    private boolean registerPoints(Match match, Player bukkit, double points) {
        if (bukkit == null || points == Score.ZERO) {
            return false;
        }

        GamePlayer player = match.getGame().getPlayer(bukkit);
        if (player == null) {
            return false;
        }

        GoalHolder competitor = match.findWinnerByPlayer(player);
        if (competitor != null) {
            Score score = this.getScore(competitor);

            if (score != null) {
                score.incrementScore(player, points);
                return true;
            }
        }

        return false;
    }
}
