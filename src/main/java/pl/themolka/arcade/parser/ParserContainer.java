package pl.themolka.arcade.parser;

import pl.themolka.arcade.util.Container;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ParserContainer implements Container<Parser> {
    private final Map<Class<? extends Parser>, Parser<?>> parsers = new LinkedHashMap<>();

    public ParserContainer() {
        this.parsers.put(TextParser.class, new TextParser()); // text is default
        this.parsers.put(IdParser.class, new IdParser());
    }

    @Override
    public Class<Parser> getType() {
        return Parser.class;
    }

    public boolean contains(Class<? extends Parser> clazz) {
        return this.parsers.containsKey(clazz);
    }

    public boolean containsParser(Parser<?> parser) {
        return this.parsers.containsValue(parser);
    }

    public IdParser getIdParser() {
        return this.getParser(IdParser.class);
    }

    public <T extends Parser<?>> T getParser(Class<T> clazz) {
        return (T) this.parsers.get(clazz);
    }

    public Set<Class<? extends Parser>> getParserClasses() {
        return this.parsers.keySet();
    }

    public Collection<Parser<?>> getParsers() {
        return this.parsers.values();
    }

    public TextParser getTextParser() {
        return this.getParser(TextParser.class);
    }

    public void register(ParserContainer container) {
        Collection<Parser<?>> maps = container.getParsers();
        this.register(maps.toArray(new Parser<?>[maps.size()]));
    }

    public void register(Parser<?>... parsers) {
        for (Parser<?> parser : parsers) {
            if (!this.containsParser(parser)) {
                this.parsers.put(parser.getClass(), parser);
            }
        }
    }
}
