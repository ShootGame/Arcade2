package pl.themolka.arcade.goal;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;

/**
 * Called when a `Goal` is being completed.
 */
public class GoalCompleteEvent extends GoalEvent implements Cancelable {
    private boolean cancel;
    private final GoalHolder completer;

    private GoalCompleteEvent(ArcadePlugin plugin, Goal goal, GoalHolder completer) {
        super(plugin, goal);

        this.completer = completer;
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

    public boolean hasCompleter() {
        return this.completer != null;
    }

    public static GoalCompleteEvent call(ArcadePlugin plugin, Goal goal) {
        return call(plugin, goal, null);
    }

    public static GoalCompleteEvent call(ArcadePlugin plugin, Goal goal, GoalHolder completer) {
        return plugin.getEventBus().postEvent(new GoalCompleteEvent(plugin, goal, completer));
    }
}
