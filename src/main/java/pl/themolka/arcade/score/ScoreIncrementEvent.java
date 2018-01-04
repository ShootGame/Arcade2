package pl.themolka.arcade.score;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.goal.GoalHolder;

public class ScoreIncrementEvent extends ScoreEvent implements Cancelable {
    private boolean cancel;
    private GoalHolder completer;
    private int points;

    public ScoreIncrementEvent(ArcadePlugin plugin, Score score, GoalHolder completer, int points) {
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

    public GoalHolder getCompleter() {
        return this.completer;
    }

    public int getNewScore() {
        return this.getScore().getScore() + this.getPoints();
    }

    public int getPoints() {
        return this.points;
    }

    public boolean hasCompleter() {
        return this.completer != null;
    }

    public void setCompleter(GoalHolder completer) {
        this.completer = completer;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
