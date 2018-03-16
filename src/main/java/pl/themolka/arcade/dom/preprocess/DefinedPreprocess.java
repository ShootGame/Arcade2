package pl.themolka.arcade.dom.preprocess;

import pl.themolka.arcade.dom.Content;

import java.util.List;

public interface DefinedPreprocess<T extends Content, K> {
    List<T> define(K k);

    void invoke(T t) throws PreprocessException;
}
