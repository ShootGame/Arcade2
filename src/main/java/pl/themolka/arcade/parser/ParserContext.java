package pl.themolka.arcade.parser;

public class ParserContext {
    private final ParserManager parsers;

    public ParserContext(ParserManager parsers) {
        this.parsers = parsers;
    }

    public <T extends Enum<T>> EnumParser<T> enumType(Class<T> enumType) throws ParserNotSupportedException {
        try {
            return new EnumParser<>(enumType);
        } catch (NullPointerException npe) {
            throw new ParserNotSupportedException(npe);
        }
    }

    public ParserContainer getContainer() {
        return this.parsers.getContainer();
    }

    public IdParser id() throws ParserNotSupportedException {
        return this.getContainer().getIdParser();
    }

    public <T extends Parser<?>> T of(Class<T> clazz) throws ParserNotSupportedException {
        return this.getContainer().getParser(clazz);
    }

    public boolean supports(Class<? extends Parser<?>> clazz) {
        return this.getContainer().contains(clazz);
    }

    public boolean supportsType(Class<?> type) {
        return this.parsers.supportsType(type);
    }

    // Text is the default value, so we can reference it here.
    public TextParser text() throws ParserNotSupportedException {
        return this.getContainer().getTextParser();
    }

    public <T> Parser<T> type(Class<T> type) throws ParserNotSupportedException  {
        return this.parsers.forType(type);
    }
}
