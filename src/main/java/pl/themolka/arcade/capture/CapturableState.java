package pl.themolka.arcade.capture;

import org.apache.commons.lang3.builder.ToStringStyle;

public abstract class CapturableState<V extends Capturable, E extends CapturableState> {
    public static final ToStringStyle TO_STRING_STYLE = Capturable.TO_STRING_STYLE;

    protected final CaptureGame game;
    protected final V capturable;

    public CapturableState(CaptureGame game, V capturable) {
        this.game = game;
        this.capturable = capturable;
    }

    public abstract E copy();

    public V getCapturable() {
        return this.capturable;
    }

    public String getStateName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public abstract String toString();
}
