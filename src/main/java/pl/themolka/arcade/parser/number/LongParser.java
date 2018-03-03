package pl.themolka.arcade.parser.number;

import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;

@Produces(Long.class)
public class LongParser extends NumberParser<Long> {
    public LongParser() {
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("a real number (long)");
    }

    @Override
    protected Long parse(String input) throws NumberFormatException {
        return Long.parseLong(input);
    }

    @Override
    protected Long positiveInfinity() {
        return Long.MAX_VALUE;
    }

    @Override
    protected Long negativeInfinity() {
        return Long.MIN_VALUE;
    }
}
