package pl.themolka.arcade.match;

import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.commons.session.Session;

import java.time.Duration;
import java.util.List;

public class MatchGame extends GameModule {
    public static final String METADATA_MATCH = "match";

    private Match match;

    @Override
    public void onEnable() {
        this.match = new Match(this.getPlugin(), this.getGame());

        this.getGame().setMetadata(MatchModule.class, METADATA_MATCH, this.getMatch());
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public List<Object> onListenersRegister(List<Object> register) {
        return super.onListenersRegister(register);
    }

    public Match getMatch() {
        return this.match;
    }

    public void handleBeginCommand(Session<ArcadePlayer> sender, int seconds, boolean force) {
        String message = "Starting";
        if (force) {
            message = "Force starting";
        }

        sender.sendSuccess(message + " the match in " + seconds + " seconds...");
        this.startCountdown(seconds);
    }

    public void handleEndCommand(Session<ArcadePlayer> sender, String winnerQuery, boolean draw) {
        MatchWinner winner = null;
        if (draw) {
            winner = new DrawWinner();
        } else {

        }

        sender.sendSuccess("Force ending the match...");
        this.getMatch().end(winner, true);
    }

    public void startCountdown(int seconds) {
        MatchStartCountdown countdown = new MatchStartCountdown(this.getPlugin(), Duration.ofSeconds(seconds), this.match);
        this.scheduleSyncTask(countdown);
    }
}
