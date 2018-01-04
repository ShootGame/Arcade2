package pl.themolka.arcade.goal;

import pl.themolka.arcade.ArcadePlugin;

public class GoalProgressEvent extends GoalEvent {
    private GoalHolder completer;
    private final double oldProgress;
    private final double newProgress;

    private GoalProgressEvent(ArcadePlugin plugin, Goal goal, GoalHolder completer, double oldProgress, double newProgress) {
        super(plugin, goal);

        this.completer = completer;
        this.oldProgress = oldProgress;
        this.newProgress = newProgress;
    }

    public GoalHolder getCompleter() {
        return this.completer;
    }

    public double getOldProgress() {
        return this.oldProgress;
    }

    public double getNewProgress() {
        return this.newProgress;
    }

    public static GoalProgressEvent call(ArcadePlugin plugin, Goal goal, double oldProgress) {
        return call(plugin, goal, null, oldProgress);
    }

    public static GoalProgressEvent call(ArcadePlugin plugin, Goal goal, GoalHolder completer, double oldProgress) {
        return plugin.getEventBus().postEvent(new GoalProgressEvent(plugin, goal, completer, oldProgress, goal.getProgress()));
    }
}
