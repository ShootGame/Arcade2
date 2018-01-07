package pl.themolka.arcade.capture.flag.state;

import pl.themolka.arcade.capture.CapturableState;
import pl.themolka.arcade.capture.flag.Flag;

public abstract class FlagState extends CapturableState<Flag, FlagState> {
    protected final Flag flag;

    public FlagState(Flag flag) {
        super(flag.getCaptureGame(), flag);

        this.flag = flag;
    }

    @Override
    public abstract FlagState copy();

    @Override
    public abstract String toString();

    public abstract static class Permanent extends FlagState {
        public Permanent(Flag flag) {
            super(flag);
        }
    }
}
