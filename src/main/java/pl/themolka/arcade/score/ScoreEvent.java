package pl.themolka.arcade.score;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GameEvent;

public class ScoreEvent extends GameEvent {
    private final Score score;

    public ScoreEvent(ArcadePlugin plugin, Score score) {
        super(plugin);

        this.score = score;
    }

    public int getPoints() {
        return this.getScore().getScore();
    }

    public Score getScore() {
        return this.score;
    }
}
