package pl.themolka.arcade.parser.number;

import java.util.Collections;
import java.util.List;

public class FloatParser extends NumberParser<Float> {
    @Override
    public List<Object> expect() {
        return Collections.singletonList("a decimal (float)");
    }

    @Override
    protected Float parse(String input) throws NumberFormatException {
        return Float.parseFloat(input);
    }
}
