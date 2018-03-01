package pl.themolka.arcade.parser;

public class ParserContext {
    private final ParserManager parsers;

    public ParserContext(ParserManager parsers) {
        this.parsers = parsers;
    }

    public <T extends Enum<T>> EnumParser<T> enumType(Class<T> enumType) {
        return new EnumParser<>(enumType);
    }

    public ParserContainer getContainer() {
        return this.parsers.getContainer();
    }

    public IdParser id() {
        return this.getContainer().getIdParser();
    }

    public <T extends Parser<?>> T of(Class<T> clazz) {
        return this.getContainer().getParser(clazz);
    }

    // Text is the default value, so we can reference it here.
    public TextParser text() {
        return this.getContainer().getTextParser();
    }

    public <T> Parser<T> type(Class<T> type) {
        return this.parsers.forType(type);
    }
}
