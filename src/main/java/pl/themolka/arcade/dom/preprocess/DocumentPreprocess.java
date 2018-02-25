package pl.themolka.arcade.dom.preprocess;

import pl.themolka.arcade.dom.Document;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;
import pl.themolka.arcade.parser.ParserException;

import java.util.Collections;
import java.util.List;

public abstract class DocumentPreprocess implements DefinedPreprocess<Document, Document>, Preprocess {
    @Override
    public List<Document> define(Document document) {
        return Collections.singletonList(document);
    }

    @Override
    public void preprocess(Document document) throws ParserException {
        this.invoke(document);
    }

    @Override
    public final void preprocess(Node node) throws ParserException {
    }

    @Override
    public final void preprocess(Property property) throws ParserException {
    }
}
