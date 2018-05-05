package pl.themolka.arcade.goal;

import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.util.FinitePercentage;
import pl.themolka.arcade.util.Progressive;

public class GoalProgressEvent extends GoalEvent implements Progressive {
    private Participator completer;
    private final FinitePercentage oldProgress;
    private final FinitePercentage newProgress;

    private GoalProgressEvent(Goal goal, Participator completer, FinitePercentage oldProgress, FinitePercentage newProgress) {
        super(goal);

        this.completer = completer;
        this.oldProgress = oldProgress;
        this.newProgress = newProgress;
    }

    @Override
    public FinitePercentage getProgress() {
        return this.newProgress;
    }

    public Participator getCompleter() {
        return this.completer;
    }

    public FinitePercentage getOldProgress() {
        return this.oldProgress;
    }

    public static GoalProgressEvent call(Goal goal, FinitePercentage oldProgress) {
        return call(goal, null, oldProgress);
    }

    public static GoalProgressEvent call(Goal goal, Participator completer, FinitePercentage oldProgress) {
        return goal.getPlugin().getEventBus().postEvent(new GoalProgressEvent(goal, completer, oldProgress, goal.getProgress()));
    }
}
