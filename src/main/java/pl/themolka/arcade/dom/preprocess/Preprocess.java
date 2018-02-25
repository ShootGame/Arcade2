package pl.themolka.arcade.dom.preprocess;

import pl.themolka.arcade.dom.Document;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;
import pl.themolka.arcade.parser.ParserException;

public interface Preprocess {
    void preprocess(Document document) throws ParserException;

    void preprocess(Node node) throws ParserException;

    void preprocess(Property property) throws ParserException;
}
