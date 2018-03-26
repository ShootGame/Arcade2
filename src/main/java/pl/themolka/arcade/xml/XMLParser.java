package pl.themolka.arcade.xml;

import pl.themolka.arcade.dom.EmptyElement;
import pl.themolka.arcade.parser.EnumParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.number.DoubleParser;
import pl.themolka.arcade.parser.number.IntegerParser;
import pl.themolka.arcade.parser.type.BooleanParser;
import pl.themolka.arcade.parser.type.MessageParser;

/**
 * @deprecated XML parsing will be replaced with the new DOM classes.
 * All XML parsers will be removed in the near future.
 *
 * TODO:
 * - write simple module parsing with reference handling.
 * - write observers,
 *         portal,
 *         scorebox,
 *         spawn,
 *         team parsing
 * - write core,
 *         flag,
 *         point,
 *         score,
 *         wool parsing
 */
@Deprecated
public class XMLParser {
    private static final Parser<Boolean> booleanParser = new BooleanParser();
    private static final Parser<Double> doubleParser = new DoubleParser();
    private static final Parser<Integer> integerParser = new IntegerParser();
    private static final Parser<String> messageParser = new MessageParser();

    /**
     * @deprecated {@link BooleanParser}
     */
    @Deprecated
    public static boolean parseBoolean(String input, boolean def) {
        try {
            return booleanParser.parseWithValue(EmptyElement.empty(), input).orFail();
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
            return doubleParser.parseWithValue(EmptyElement.empty(), input).orFail();
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
     * @deprecated {@link IntegerParser}
     */
    @Deprecated
    public static int parseInt(String input, int def) {
        try {
            return integerParser.parseWithValue(EmptyElement.empty(), input).orFail();
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
            return messageParser.parseWithValue(EmptyElement.empty(), message).orFail();
        } catch (ParserException ex) {
            return null;
        }
    }
}
