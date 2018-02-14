package pl.themolka.arcade.capture.wool;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.Participator;

public class WoolPlaceEvent extends WoolEvent implements Cancelable {
    private boolean cancel;
    private Participator completer;
    private final GamePlayer player;

    public WoolPlaceEvent(ArcadePlugin plugin, Wool wool, Participator completer, GamePlayer player) {
        super(plugin, wool);

        this.completer = completer;
        this.player = player;
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

    public GamePlayer getPlayer() {
        return this.player;
    }

    public void setCompleter(Participator completer) {
        this.completer = completer;
    }
}
