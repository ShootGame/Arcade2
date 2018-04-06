package pl.themolka.arcade.score;

import pl.themolka.arcade.ArcadePlugin;

public class ScoreLimitReachEvent extends ScoreEvent {
    public ScoreLimitReachEvent(ArcadePlugin plugin, Score score) {
        super(plugin, score);
    }

    public double getLimit() {
        return this.getScore().getLimit();
    }
}
