package pl.themolka.arcade.xml;

import org.jdom2.Element;
import pl.themolka.arcade.dom.EmptyElement;
import pl.themolka.arcade.parser.EnumParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserUtils;
import pl.themolka.arcade.parser.TextParser;
import pl.themolka.arcade.parser.number.ByteParser;
import pl.themolka.arcade.parser.number.DoubleParser;
import pl.themolka.arcade.parser.number.FloatParser;
import pl.themolka.arcade.parser.number.IntegerParser;
import pl.themolka.arcade.parser.number.LongParser;
import pl.themolka.arcade.parser.number.ShortParser;
import pl.themolka.arcade.parser.type.BooleanParser;
import pl.themolka.arcade.parser.type.MessageParser;

import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated XML parsing will be replaced with the new DOM classes.
 * All XML parsers will be removed in the near future.
 */
@Deprecated
public class XMLParser {
    /**
     * @deprecated {@link pl.themolka.arcade.dom.Node#children(String...)}
     */
    @Deprecated
    public static List<Element> children(Element element, String... children) {
        List<Element> results = new ArrayList<>();
        for (String child : children) {
            results.addAll(element.getChildren(child));
        }

        return results;
    }

    /**
     * @deprecated {@link pl.themolka.arcade.parser.ParserUtils#array(String)}
     */
    @Deprecated
    public static List<String> parseArray(String value) {
        return ParserUtils.array(value);
    }

    /**
     * @deprecated {@link BooleanParser}
     */
    @Deprecated
    public static boolean parseBoolean(String input) {
        return parseBoolean(input, false);
    }

    /**
     * @deprecated {@link BooleanParser}
     */
    @Deprecated
    public static boolean parseBoolean(String input, boolean def) {
        try {
            return Parsers.booleanParser()
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
    public static double parseDouble(String input) {
        return parseDouble(input, 0.0D);
    }

    /**
     * @deprecated {@link DoubleParser}
     */
    @Deprecated
    public static double parseDouble(String input, double def) {
        try {
            return Parsers.doubleParser()
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
                return key.toUpperCase().trim().replace(" ", "_").replace("-", "_");
            }
        }

        return null;
    }

    /**
     * @deprecated {@link FloatParser}
     */
    @Deprecated
    public static float parseFloat(String input) {
        return parseFloat(input, 0.0F);
    }

    /**
     * @deprecated {@link FloatParser}
     */
    @Deprecated
    public static float parseFloat(String input, float def) {
        try {
            return Parsers.floatParser()
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
    public static int parseInt(String input) {
        return parseInt(input, 0);
    }

    /**
     * @deprecated {@link IntegerParser}
     */
    @Deprecated
    public static int parseInt(String input, int def) {
        try {
            return Parsers.integerParser()
                    .parseWithValue(EmptyElement.empty(), input)
                    .orFail();
        } catch (ParserException ex) {
            return def;
        }
    }

    /**
     * @deprecated {@link LongParser}
     */
    @Deprecated
    public static long parseLong(String input) {
        return parseLong(input, 0L);
    }

    /**
     * @deprecated {@link LongParser}
     */
    @Deprecated
    public static long parseLong(String input, long def) {
        try {
            return Parsers.longParser()
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
            return Parsers.messageParser()
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
// These parsers cannot have dependencies. These parsers are non-installable.
@Deprecated
final class Parsers {
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

    public static <T extends Enum<T>> EnumParser<T> enumParser(Class<T> type) {
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
