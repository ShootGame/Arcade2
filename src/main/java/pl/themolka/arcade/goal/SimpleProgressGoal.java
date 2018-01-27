package pl.themolka.arcade.goal;

import pl.themolka.arcade.game.Game;

public abstract class SimpleProgressGoal extends SimpleGoal {
    private double initialProgress;
    private double progress;

    public SimpleProgressGoal(Game game, GoalHolder owner) {
        this(game, owner, PROGRESS_UNTOUCHED);
    }

    public SimpleProgressGoal(Game game, GoalHolder owner, double initialProgress) {
        super(game, owner);

        this.initialProgress = initialProgress;
        this.progress = initialProgress;
    }

    @Override
    public double getProgress() {
        return this.progress;
    }

    public double getInitialProgress() {
        return this.initialProgress;
    }

    public void setProgress(double progress) {
        if (progress > PROGRESS_SCORED) {
            progress = PROGRESS_SCORED;
        } else if (progress < PROGRESS_UNTOUCHED) {
            progress = PROGRESS_UNTOUCHED;
        }

        this.progress = progress;
    }
}
