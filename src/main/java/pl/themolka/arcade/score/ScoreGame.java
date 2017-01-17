package pl.themolka.arcade.score;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.match.MatchGame;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.match.MatchWinner;

public class ScoreGame extends GameModule {
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
            winner.addGoal(score);
        }
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
        if (this.getMatch() == null) {
            return;
        }

        MatchWinner winner = this.getMatch().findWinnerByPlayer(event.getEntity());
        if (winner == null) {
            return;
        }

        Score score = this.getScore(winner);
        if (score != null) {
            score.incrementScore(this.getKills());
        }
    }
}
