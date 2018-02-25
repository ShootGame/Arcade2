package pl.themolka.arcade.parser.number;

import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.List;

@Produces(Double.class)
public class DoubleParser extends NumberParser<Double> {
    public DoubleParser() {
    }

    @Override
    public List<Object> expect() {
        return Collections.singletonList("a decimal (double)");
    }

    @Override
    protected Double parse(String input) throws NumberFormatException {
        return Double.parseDouble(input);
    }
}
