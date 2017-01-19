package pl.themolka.arcade.score;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.goal.Goal;
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
    private Match match;

    public ScoreGame(int kills, int limit) {
        this.kills = kills;
        this.limit = limit;
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

            if (this.getScore(winner) == null) {
                winner.addGoal(score);
            }
        }
    }

    @Override
    public MatchWinner getDynamicWinner() {
        List<MatchWinner> winners = this.getDynamicWinners();
        if (winners == null) {
            return null;
        }

        if (winners.size() == 1) {
            return winners.get(0);
        } else {
            return this.getMatch().getDrawWinner();
        }
    }

    public List<MatchWinner> getDynamicWinners() {
        int highestScore = 0;

        List<MatchWinner> results = new ArrayList<>();
        for (MatchWinner winner : this.getMatch().getWinners()) {
            if (winner.isWinning()) {
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
                results.add(score.getOwner());
            }
        }

        if (highestScore != 0) {
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
}
