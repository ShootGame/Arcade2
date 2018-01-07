package pl.themolka.arcade.capture.point;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.score.Score;

public class PointPointsEvent extends PointScoreEvent implements Cancelable {
    private boolean cancel;
    private final double oldScore;
    private double points;

    public PointPointsEvent(ArcadePlugin plugin, Point point, Score score, double points) {
        super(plugin, point, score);

        this.oldScore = score.getScore();
        this.points = points;
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }

    public double getOldScore() {
        return this.oldScore;
    }

    public double getNewScore() {
        return this.getOldScore() + this.getPoints();
    }

    public double getPoints() {
        return this.points;
    }

    public void setPoints(double points) {
        this.points = points;
    }
}
