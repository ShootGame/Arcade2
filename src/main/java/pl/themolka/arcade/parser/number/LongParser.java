package pl.themolka.arcade.parser.number;

import java.util.Collections;
import java.util.List;

public class LongParser extends NumberParser<Long> {
    @Override
    public List<Object> expect() {
        return Collections.singletonList("a real number (long)");
    }

    @Override
    protected Long parse(String input) throws NumberFormatException {
        return Long.parseLong(input);
    }
}
