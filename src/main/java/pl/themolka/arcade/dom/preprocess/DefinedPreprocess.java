package pl.themolka.arcade.dom.preprocess;

import pl.themolka.arcade.parser.ParserException;

import java.util.List;

public interface DefinedPreprocess<T, K> {
    List<T> define(K k);

    void invoke(T t) throws ParserException;
}
