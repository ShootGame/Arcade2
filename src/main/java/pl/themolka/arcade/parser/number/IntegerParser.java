package pl.themolka.arcade.parser.number;

import java.util.Collections;
import java.util.List;

public class IntegerParser extends NumberParser<Integer> {
    @Override
    public List<Object> expect() {
        return Collections.singletonList("a real number (integer)");
    }

    @Override
    protected Integer parse(String input) throws NumberFormatException {
        return Integer.parseInt(input);
    }
}
