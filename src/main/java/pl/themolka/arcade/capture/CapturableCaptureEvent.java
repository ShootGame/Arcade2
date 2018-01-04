package pl.themolka.arcade.capture;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.goal.GoalHolder;

public class CapturableCaptureEvent extends CapturableEvent implements Cancelable {
    private boolean cancel;
    private GoalHolder completer;

    public CapturableCaptureEvent(ArcadePlugin plugin, Capturable capturable, GoalHolder completer) {
        super(plugin, capturable);

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

    public void setCompleter(GoalHolder completer) {
        this.completer = completer;
    }
}
