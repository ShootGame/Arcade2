package pl.themolka.arcade.parser;

public class ParserContext {
    private final ParserContainer container;

    public ParserContext(ParserContainer container) {
        this.container = container;
    }

    public <T extends Parser<T>> T parser(Class<T> clazz) {
        return this.container.getParser(clazz);
    }
}
