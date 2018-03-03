package pl.themolka.arcade.parser.number;

import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;

@Produces(Short.class)
public class ShortParser extends NumberParser<Short> {
    public ShortParser() {
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("a real number between " + Short.MIN_VALUE + " and " + Short.MAX_VALUE + " (short)");
    }

    @Override
    protected Short parse(String input) throws NumberFormatException {
        return Short.parseShort(input);
    }

    @Override
    protected Short positiveInfinity() {
        return Short.MAX_VALUE;
    }

    @Override
    protected Short negativeInfinity() {
        return Short.MIN_VALUE;
    }
}
