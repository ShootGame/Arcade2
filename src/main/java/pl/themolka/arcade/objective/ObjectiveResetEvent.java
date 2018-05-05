package pl.themolka.arcade.objective;

import pl.themolka.arcade.event.Cancelable;

public class ObjectiveResetEvent extends ObjectiveEvent implements Cancelable {
    private boolean cancel;

    public ObjectiveResetEvent(Objective objective) {
        super(objective);
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
