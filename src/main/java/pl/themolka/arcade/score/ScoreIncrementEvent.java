package pl.themolka.arcade.score;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.commons.event.Cancelable;

public class ScoreIncrementEvent extends ScoreEvent implements Cancelable {
    private boolean cancel;
    private int points;

    public ScoreIncrementEvent(ArcadePlugin plugin, Score score, int points) {
        super(plugin, score);

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

    public int getNewScore() {
        return this.getScore().getScore() + this.getPoints();
    }

    public int getPoints() {
        return this.points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
