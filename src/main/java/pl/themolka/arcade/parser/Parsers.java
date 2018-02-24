package pl.themolka.arcade.parser;

import pl.themolka.arcade.parser.number.ByteParser;
import pl.themolka.arcade.parser.number.DoubleParser;
import pl.themolka.arcade.parser.number.FloatParser;
import pl.themolka.arcade.parser.number.IntegerParser;
import pl.themolka.arcade.parser.number.LongParser;
import pl.themolka.arcade.parser.number.ShortParser;
import pl.themolka.arcade.parser.type.BooleanParser;
import pl.themolka.arcade.parser.type.MessageParser;

/**
 * Temporary fast and easy access to the numeric and standard parsers.
 * @deprecated This class is temporary.
 */
@Deprecated
public final class Parsers {
    private Parsers() {
    }

    //
    // Numeric Types
    //

    private static final ByteParser byteParser = new ByteParser();
    public static ByteParser byteParser() {
        return byteParser;
    }

    private static final DoubleParser doubleParser = new DoubleParser();
    public static DoubleParser doubleParser() {
        return doubleParser;
    }

    private static final FloatParser floatParser = new FloatParser();
    public static FloatParser floatParser() {
        return floatParser;
    }

    private static final IntegerParser integerParser = new IntegerParser();
    public static IntegerParser integerParser() {
        return integerParser;
    }

    private static final LongParser longParser = new LongParser();
    public static LongParser longParser() {
        return longParser;
    }

    private static final ShortParser shortParser = new ShortParser();
    public static ShortParser shortParser() {
        return shortParser;
    }

    //
    // Standard Types
    //

    private static final BooleanParser booleanParser = new BooleanParser();
    public static BooleanParser booleanParser() {
        return booleanParser;
    }

    public static <E extends Enum<E>> EnumParser<E> enumParser(Class<E> type) {
        return new EnumParser(type);
    }

    private static final MessageParser messageParser = new MessageParser();
    public static MessageParser messageParser() {
        return messageParser;
    }

    private static final TextParser textParser = new TextParser();
    public static TextParser textParser() {
        return textParser;
    }
}
