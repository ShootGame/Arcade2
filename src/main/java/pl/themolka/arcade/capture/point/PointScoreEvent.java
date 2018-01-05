package pl.themolka.arcade.capture.point;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.score.Score;

public class PointScoreEvent extends PointEvent {
    private final Score score;

    public PointScoreEvent(ArcadePlugin plugin, Point point, Score score) {
        super(plugin, point);

        this.score = score;
    }

    public Score getScore() {
        return this.score;
    }
}
