package pl.themolka.arcade.objective;

import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.game.Participator;

public class ObjectiveCompleteEvent extends ObjectiveEvent implements Cancelable {
    private boolean cancel;
    private final Participator completer;

    public ObjectiveCompleteEvent(Objective objective, Participator completer) {
        super(objective);

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
}
