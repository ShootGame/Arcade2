package pl.themolka.arcade.capture.flag.state;

import org.apache.commons.lang3.builder.ToStringBuilder;
import pl.themolka.arcade.capture.flag.Flag;

public class NeutralState extends FlagState.Permanent {
    public NeutralState(Flag flag) {
        super(flag);
    }

    @Override
    public FlagState copy() {
        return new NeutralState(this.flag);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE)
                .append("flag", this.flag)
                .build();
    }
}
