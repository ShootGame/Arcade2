package pl.themolka.arcade.objective.wool;

import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.Participator;

public class WoolPlaceEvent extends WoolEvent implements Cancelable {
    private boolean cancel;
    private final GamePlayer completer;
    private final Participator participator;

    public WoolPlaceEvent(Wool wool, GamePlayer completer, Participator participator) {
        super(wool);

        this.completer = completer;
        this.participator = participator;
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }

    public GamePlayer getCompleter() {
        return this.completer;
    }

    public Participator getParticipator() {
        return this.participator;
    }
}
