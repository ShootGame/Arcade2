package pl.themolka.arcade.parser;

public class ParserContext {
    private final ParserManager parsers;

    public ParserContext(ParserManager parsers) {
        this.parsers = parsers;
    }

    public <T extends Enum<T>> EnumParser<T> enumType(Class<T> enumType) {
        return EnumParser.create(enumType);
    }

    public ParserContainer getContainer() {
        return this.parsers.getContainer();
    }

    public <T extends Parser<?>> T of(Class<T> clazz) {
        return this.getContainer().getParser(clazz);
    }

    public <T> Parser<T> type(Class<T> type) {
        return this.parsers.forType(type);
    }
}
