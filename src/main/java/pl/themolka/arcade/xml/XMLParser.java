package pl.themolka.arcade.xml;

import pl.themolka.arcade.dom.EmptyElement;
import pl.themolka.arcade.parser.EnumParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.number.DoubleParser;
import pl.themolka.arcade.parser.number.FloatParser;
import pl.themolka.arcade.parser.number.IntegerParser;
import pl.themolka.arcade.parser.type.BooleanParser;
import pl.themolka.arcade.parser.type.MessageParser;

/**
 * @deprecated XML parsing will be replaced with the new DOM classes.
 * All XML parsers will be removed in the near future.
 *
 * TODO:
 * - write simple module parsing with reference handling.
 * - write kit content parsing
 * - write filter, kit, observers, portal, scorebox, spawn and team parsing.
 */
@Deprecated
public class XMLParser {
    private static final Parsers parsers = new Parsers();

    /**
     * @deprecated {@link BooleanParser}
     */
    @Deprecated
    public static boolean parseBoolean(String input, boolean def) {
        try {
            return parsers.booleanParser()
                    .parseWithValue(EmptyElement.empty(), input)
                    .orFail();
        } catch (ParserException ex) {
            return def;
        }
    }

    /**
     * @deprecated {@link DoubleParser}
     */
    @Deprecated
    public static double parseDouble(String input, double def) {
        try {
            return parsers.doubleParser()
                    .parseWithValue(EmptyElement.empty(), input)
                    .orFail();
        } catch (ParserException ex) {
            return def;
        }
    }

    /**
     * @deprecated {@link EnumParser}
     */
    @Deprecated
    public static String parseEnumValue(String key) {
        if (key != null) {
            key = key.trim();

            if (!key.isEmpty()) {
                return EnumParser.toEnumValue(key);
            }
        }

        return null;
    }

    /**
     * @deprecated {@link FloatParser}
     */
    @Deprecated
    public static float parseFloat(String input, float def) {
        try {
            return parsers.floatParser()
                    .parseWithValue(EmptyElement.empty(), input)
                    .orFail();
        } catch (ParserException ex) {
            return def;
        }
    }

    /**
     * @deprecated {@link IntegerParser}
     */
    @Deprecated
    public static int parseInt(String input, int def) {
        try {
            return parsers.integerParser()
                    .parseWithValue(EmptyElement.empty(), input)
                    .orFail();
        } catch (ParserException ex) {
            return def;
        }
    }

    /**
     * @deprecated {@link MessageParser}
     */
    @Deprecated
    public static String parseMessage(String message) {
        try {
            return parsers.messageParser()
                    .parseWithValue(EmptyElement.empty(), message)
                    .orFail();
        } catch (ParserException ex) {
            return null;
        }
    }
}

/**
 * Temporary fast and easy access to the numeric and standard parsers.
 * @deprecated This class is temporary and should be never used.
 */
// These parsers are non-installable and cannot have dependencies.
@Deprecated
final class Parsers {
    private final BooleanParser booleanParser = new BooleanParser();
    private final DoubleParser doubleParser = new DoubleParser();
    private final FloatParser floatParser = new FloatParser();
    private final IntegerParser integerParser = new IntegerParser();
    private final MessageParser messageParser = new MessageParser();

    protected Parsers() {
    }

    public BooleanParser booleanParser() {
        return this.booleanParser;
    }

    public DoubleParser doubleParser() {
        return this.doubleParser;
    }

    public FloatParser floatParser() {
        return this.floatParser;
    }

    public IntegerParser integerParser() {
        return this.integerParser;
    }

    public MessageParser messageParser() {
        return this.messageParser;
    }
}
