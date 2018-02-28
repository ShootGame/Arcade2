package pl.themolka.arcade.parser.number;

import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;

@Produces(Double.class)
public class DoubleParser extends NumberParser<Double> {
    public DoubleParser() {
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("a decimal (double)");
    }

    @Override
    protected Double parse(String input) throws NumberFormatException {
        return Double.parseDouble(input);
    }
}
