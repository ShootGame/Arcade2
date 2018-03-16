package pl.themolka.arcade.dom.preprocess;

import pl.themolka.arcade.dom.Document;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;

import java.util.Collections;
import java.util.List;

public abstract class DocumentPreprocess implements DefinedPreprocess<Document, Document>, Preprocess {
    @Override
    public List<Document> define(Document document) {
        return Collections.singletonList(document);
    }

    @Override
    public void preprocess(Document document) throws PreprocessException {
        this.invoke(document);
    }

    @Override
    public final void preprocess(Node node) throws PreprocessException {
        throw new PreprocessNotSupportedException(node);
    }

    @Override
    public final void preprocess(Property property) throws PreprocessException {
        throw new PreprocessNotSupportedException(property);
    }
}
