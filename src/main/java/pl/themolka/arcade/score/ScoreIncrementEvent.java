package pl.themolka.arcade.score;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.game.Participator;

public class ScoreIncrementEvent extends ScoreEvent implements Cancelable {
    private boolean cancel;
    private Participator completer;
    private double points;

    public ScoreIncrementEvent(ArcadePlugin plugin, Score score, Participator completer, double points) {
        super(plugin, score);

        this.completer = completer;
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

    public Participator getCompleter() {
        return this.completer;
    }

    public double getNewScore() {
        return this.getScore().getScore() + this.getPoints();
    }

    public double getPoints() {
        return this.points;
    }

    public boolean hasCompleter() {
        return this.completer != null;
    }

    public void setCompleter(Participator completer) {
        this.completer = completer;
    }

    public void setPoints(double points) {
        this.points = points;
    }
}
