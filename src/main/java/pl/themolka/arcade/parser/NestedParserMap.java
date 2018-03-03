package pl.themolka.arcade.parser;

import java.util.LinkedHashMap;

/**
 * A map holding nested parsers by their name given in {@link NestedParserMap}.
 */
public class NestedParserMap<T extends Parser<?>> extends LinkedHashMap<String, T> {
    private final ParserContext context;

    public NestedParserMap(ParserContext context) {
        this.context = context;
    }

    public T parse(String name) {
        return this.get(name);
    }

    public void scan(Class<?> clazz) {
        for (Class<?> nested : clazz.getDeclaredClasses()) {
            this.scanClass(nested);
        }
    }

    protected void scanClass(Class<?> clazz) {
        NestedParserName nameProvider = clazz.getDeclaredAnnotation(NestedParserName.class);
        if (nameProvider == null || !Parser.class.isAssignableFrom(clazz)) {
            return;
        }

        String[] names = nameProvider.value();
        if (names != null) {
            T parser = this.context.of((Class<T>) clazz);
            for (String name : names) {
                this.put(name, parser);
            }
        }
    }
}
