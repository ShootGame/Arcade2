package pl.themolka.arcade.parser;

public class ParserContext {
    private final ParserManager source;

    public ParserContext(ParserManager source) {
        this.source = source;
    }

    public <T extends Enum<T>> Parser<T> enumType(Class<T> type) throws ParserNotSupportedException {
        return this.source.forEnumType(type);
    }

    public ParserContainer getContainer() {
        return this.source.getContainer();
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
        return this.source.supportsType(type);
    }

    // Text is the default value, so we can reference it here.
    public TextParser text() throws ParserNotSupportedException {
        return this.getContainer().getTextParser();
    }

    public <T> Parser<T> type(Class<T> type) throws ParserNotSupportedException  {
        return this.source.forType(type);
    }

    public static class Factory {
        public ParserContext createContext(ParserManager source) {
            return new ParserContext(source);
        }
    }
}
