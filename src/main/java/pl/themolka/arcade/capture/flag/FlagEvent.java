package pl.themolka.arcade.capture.flag;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.capture.CapturableEvent;

public class FlagEvent extends CapturableEvent {
    private final Flag flag;

    public FlagEvent(ArcadePlugin plugin, Flag flag) {
        super(plugin, flag);

        this.flag = flag;
    }

    public Flag getFlag() {
        return this.flag;
    }
}
