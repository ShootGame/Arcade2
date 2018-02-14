package pl.themolka.arcade.capture;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.game.Participator;

public class CapturableCaptureEvent extends CapturableEvent implements Cancelable {
    private boolean cancel;
    private Participator completer;

    public CapturableCaptureEvent(ArcadePlugin plugin, Capturable capturable, Participator completer) {
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

    public Participator getCompleter() {
        return this.completer;
    }

    public boolean hasCompleter() {
        return this.completer != null;
    }

    public void setCompleter(Participator completer) {
        this.completer = completer;
    }
}
