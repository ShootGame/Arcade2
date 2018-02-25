package pl.themolka.arcade.parser.number;

import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.List;

@Produces(Float.class)
public class FloatParser extends NumberParser<Float> {
    public FloatParser() {
    }

    @Override
    public List<Object> expect() {
        return Collections.singletonList("a decimal (float)");
    }

    @Override
    protected Float parse(String input) throws NumberFormatException {
        return Float.parseFloat(input);
    }
}
