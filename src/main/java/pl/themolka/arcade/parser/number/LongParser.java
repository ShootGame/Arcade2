package pl.themolka.arcade.parser.number;

import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.List;

@Produces(Long.class)
public class LongParser extends NumberParser<Long> {
    public LongParser() {
    }

    @Override
    public List<Object> expect() {
        return Collections.singletonList("a real number (long)");
    }

    @Override
    protected Long parse(String input) throws NumberFormatException {
        return Long.parseLong(input);
    }
}
