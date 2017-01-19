package pl.themolka.arcade.goal;

import pl.themolka.arcade.ArcadePlugin;

public class GoalProgressEvent extends GoalEvent {
    private final double oldProgress;

    public GoalProgressEvent(ArcadePlugin plugin, Goal goal, double oldProgress) {
        super(plugin, goal);

        this.oldProgress = oldProgress;
    }

    public double getOldProgress() {
        return this.oldProgress;
    }
}
