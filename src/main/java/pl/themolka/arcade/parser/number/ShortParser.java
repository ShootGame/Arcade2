package pl.themolka.arcade.parser.number;

import java.util.Collections;
import java.util.List;

public class ShortParser extends NumberParser<Short> {
    public ShortParser() {
    }

    @Override
    public List<Object> expect() {
        return Collections.singletonList("a real number between " + Short.MIN_VALUE + " and " + Short.MAX_VALUE + " (short)");
    }

    @Override
    protected Short parse(String input) throws NumberFormatException {
        return Short.parseShort(input);
    }
}
