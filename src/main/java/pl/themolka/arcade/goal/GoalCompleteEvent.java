package pl.themolka.arcade.goal;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;

/**
 * Called when a `Goal` is being completed.
 */
public class GoalCompleteEvent extends GoalEvent implements Cancelable {
    private boolean cancel;

    public GoalCompleteEvent(ArcadePlugin plugin, Goal goal) {
        super(plugin, goal);
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }

    public static GoalCompleteEvent call(ArcadePlugin plugin, Goal goal) {
        return plugin.getEventBus().postEvent(new GoalCompleteEvent(plugin, goal));
    }
}
