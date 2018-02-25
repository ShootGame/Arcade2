package pl.themolka.arcade.parser.number;

import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.List;

@Produces(Byte.class)
public class ByteParser extends NumberParser<Byte> {
    public ByteParser() {
    }

    @Override
    public List<Object> expect() {
        return Collections.singletonList("a real number between " + Byte.MIN_VALUE + " and " + Byte.MAX_VALUE + " (byte)");
    }

    @Override
    protected Byte parse(String input) throws NumberFormatException {
        return Byte.valueOf(input);
    }
}
