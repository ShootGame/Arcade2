package pl.themolka.arcade.leak;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.goal.GoalHolder;

public class LeakableLeakEvent extends LeakableEvent implements Cancelable {
    private boolean cancel;
    private GoalHolder completer;

    public LeakableLeakEvent(ArcadePlugin plugin, Leakable leakable, GoalHolder completer) {
        super(plugin, leakable);
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

    public void setCompleter(GoalHolder completer) {
        this.completer = completer;
    }
}
