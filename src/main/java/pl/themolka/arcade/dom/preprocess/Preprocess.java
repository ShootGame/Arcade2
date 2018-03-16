package pl.themolka.arcade.dom.preprocess;

import pl.themolka.arcade.dom.Document;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;

public interface Preprocess {
    void preprocess(Document document) throws PreprocessException;

    void preprocess(Node node) throws PreprocessException;

    void preprocess(Property property) throws PreprocessException;
}
