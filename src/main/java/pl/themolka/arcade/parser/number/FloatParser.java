package pl.themolka.arcade.parser.number;

import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;

@Produces(Float.class)
public class FloatParser extends NumberParser<Float> {
    public FloatParser() {
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("a decimal (float)");
    }

    @Override
    protected Float parse(String input) throws NumberFormatException {
        return Float.parseFloat(input);
    }

    @Override
    protected Float positiveInfinity() {
        return Float.MAX_VALUE;
    }

    @Override
    protected Float negativeInfinity() {
        return Float.MIN_VALUE;
    }
}
