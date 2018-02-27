package pl.themolka.arcade.destroy;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;

public class DestroyableResetEvent extends DestroyableEvent implements Cancelable {
    private boolean cancel;

    public DestroyableResetEvent(ArcadePlugin plugin, Destroyable destroyable) {
        super(plugin, destroyable);
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
