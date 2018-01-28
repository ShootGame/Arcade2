package pl.themolka.arcade.score;

import net.engio.mbassy.listener.Handler;
import org.bukkit.ChatColor;
import org.jdom2.Element;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.life.PlayerDeathEvent;
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

    private final ScoreConfig config;
    private final Map<String, Element> competitors;

    private Match match;

    public ScoreGame(ScoreConfig config, Map<String, Element> competitors) {
        this.config = config;
        this.competitors = competitors;
    }

    @Override
    public void onEnable() {
        GameModule module = this.getGame().getModule(MatchModule.class);
        if (module == null || !(module instanceof MatchGame)) {
            return;
        }

        this.match = ((MatchGame) module).getMatch();
        this.getMatch().registerDynamicWinnable(this);

        for (Map.Entry<String, Element> competitor : this.competitors.entrySet()) {
            MatchWinner winner = this.getMatch().findWinnerById(competitor.getKey());
            if (winner == null) {
                continue;
            }

            ScoreConfig config = ScoreConfig.parse(competitor.getValue(), this.config);
            if (config != null) {
                Score score = new Score(this, winner, config);

                // Make sure that Score is only ONE per GoalHolder
                if (score.isCompletableBy(winner) && this.getScore(winner) == null) {
                    this.byOwner.put(winner, score);
                    winner.addGoal(score);
                }
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

    public ScoreConfig getConfig() {
        return this.config;
    }

    public Match getMatch() {
        return this.match;
    }

    public Score getScore(GoalHolder holder) {
        return this.byOwner.get(holder);
    }

    @Handler(priority = Priority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Match match = this.getMatch();

        if (match != null) {
            // kill registration
            boolean wasKill = this.registerPoints(match, event.getKiller(), true);

            // death registration
            if (!wasKill) {
                this.registerPoints(match, event.getVictim(), false);
            }
        }
    }

    @Handler(priority = Priority.HIGHER)
    public void onScoreLimitReach(ScoreLimitReachEvent event) {
        this.getMatch().sendGoalMessage(ChatColor.YELLOW + "The score limit of " + ChatColor.GOLD + ChatColor.BOLD +
                Math.round(event.getLimit()) + " points" + ChatColor.RESET + ChatColor.YELLOW + " for " + ChatColor.GOLD +
                event.getScore().getOwner().getTitle() + ChatColor.YELLOW + " has been reached.");
    }

    private boolean registerPoints(Match match, GamePlayer player, boolean killReward) {
        if (player == null) {
            return false;
        }

        GoalHolder competitor = match.findWinnerByPlayer(player);
        if (competitor != null) {
            Score score = this.getScore(competitor);

            if (score != null) {
                double value = killReward ? score.getConfig().getKillReward() : -score.getConfig().getDeathLoss();

                if (value != Score.ZERO) {
                    score.incrementScore(player, value);
                }
                return true;
            }
        }

        return false;
    }
}
