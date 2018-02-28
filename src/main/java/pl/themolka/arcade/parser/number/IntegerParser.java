package pl.themolka.arcade.parser.number;

import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;

@Produces(Integer.class)
public class IntegerParser extends NumberParser<Integer> {
    public IntegerParser() {
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("a real number (integer)");
    }

    @Override
    protected Integer parse(String input) throws NumberFormatException {
        return Integer.parseInt(input);
    }
}
