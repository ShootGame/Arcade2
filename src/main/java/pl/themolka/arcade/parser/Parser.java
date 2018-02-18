package pl.themolka.arcade.parser;

import pl.themolka.arcade.util.NamedValue;

public interface Parser<T extends NamedValue<?, ?>, R> {
    R parse(T t) throws ParserException;
}
