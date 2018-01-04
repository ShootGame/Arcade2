package pl.themolka.arcade.capture;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;

public class CapturableResetEvent extends CapturableEvent implements Cancelable {
    private boolean cancel;

    public CapturableResetEvent(ArcadePlugin plugin, Capturable capturable) {
        super(plugin, capturable);
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }
}
