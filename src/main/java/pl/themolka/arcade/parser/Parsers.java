package pl.themolka.arcade.parser;

/**
 * Default parsers
 */
public final class Parsers {
    private Parsers() {
    }

    private static final BooleanParser booleanParser = new BooleanParser();
    private static final MaterialParser materialParser = new MaterialParser();
    private static final MessageParser messageParser = new MessageParser();
    private static final TextParser textParser = new TextParser();

    public static BooleanParser booleanParser() {
        return booleanParser;
    }

    public static NumberParser<Byte> byteParser() {
        return NumberParser.BYTE;
    }

    public static NumberParser<Double> doubleParser() {
        return NumberParser.DOUBLE;
    }

    public static <E extends Enum<E>> EnumParser<E> enumParser(Class<E> type) {
        return new EnumParser<>(type);
    }

    public static NumberParser<Float> floatParser() {
        return NumberParser.FLOAT;
    }

    public static NumberParser<Integer> intParser() {
        return NumberParser.INT;
    }

    public static NumberParser<Long> longParser() {
        return NumberParser.LONG;
    }

    public static MaterialParser materialParser() {
        return materialParser;
    }

    public static MessageParser messageParser() {
        return messageParser;
    }

    public static NumberParser<Short> shortParser() {
        return NumberParser.SHORT;
    }

    public static TextParser textParser() {
        return textParser;
    }
}
