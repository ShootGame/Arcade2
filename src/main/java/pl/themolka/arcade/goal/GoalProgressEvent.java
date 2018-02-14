package pl.themolka.arcade.goal;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.Participator;

public class GoalProgressEvent extends GoalEvent {
    private Participator completer;
    private final double oldProgress;
    private final double newProgress;

    private GoalProgressEvent(ArcadePlugin plugin, Goal goal, Participator completer, double oldProgress, double newProgress) {
        super(plugin, goal);

        this.completer = completer;
        this.oldProgress = oldProgress;
        this.newProgress = newProgress;
    }

    public Participator getCompleter() {
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

    public static GoalProgressEvent call(ArcadePlugin plugin, Goal goal, Participator completer, double oldProgress) {
        return plugin.getEventBus().postEvent(new GoalProgressEvent(plugin, goal, completer, oldProgress, goal.getProgress()));
    }
}
