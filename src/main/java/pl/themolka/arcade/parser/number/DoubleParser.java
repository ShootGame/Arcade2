package pl.themolka.arcade.parser.number;

import java.util.Collections;
import java.util.List;

public class DoubleParser extends NumberParser<Double> {
    @Override
    public List<Object> expect() {
        return Collections.singletonList("a decimal (double)");
    }

    @Override
    protected Double parse(String input) throws NumberFormatException {
        return Double.parseDouble(input);
    }
}
