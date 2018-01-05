package pl.themolka.arcade.capture.wool;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.capture.CapturableEvent;

public class WoolEvent extends CapturableEvent {
    private final Wool wool;

    public WoolEvent(ArcadePlugin plugin, Wool wool) {
        super(plugin, wool);

        this.wool = wool;
    }

    public Wool getWool() {
        return this.wool;
    }
}
