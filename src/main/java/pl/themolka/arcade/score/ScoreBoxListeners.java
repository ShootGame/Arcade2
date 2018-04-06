package pl.themolka.arcade.score;

import net.engio.mbassy.listener.Handler;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.session.PlayerMoveEvent;

public class ScoreBoxListeners {
    private final ScoreGame game;

    public ScoreBoxListeners(ScoreGame game) {
        this.game = game;
    }

    @Handler(priority = Priority.LOWEST)
    public void onPlayerEnterScoreBox(PlayerMoveEvent event) {
        if (event.isCanceled() || !this.game.hasAnyScoreBoxes()) {
            return;
        }

        GamePlayer player = event.getGamePlayer();
        ScoreBox scoreBox = this.game.getScoreBox(event.getTo().toVector());

        if (scoreBox == null || !scoreBox.canScore(player)) {
            return;
        }

        Participator participator = this.game.getMatch().findWinnerByPlayer(player);
        if (participator != null) {
            Score score = this.game.getScore(participator);
            if (score != null) {
                scoreBox.score(score, player);
            }
        }
    }
}
