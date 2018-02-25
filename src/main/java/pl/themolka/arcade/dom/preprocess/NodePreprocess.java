package pl.themolka.arcade.dom.preprocess;

import pl.themolka.arcade.dom.Document;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Parent;
import pl.themolka.arcade.dom.Property;
import pl.themolka.arcade.parser.ParserException;

import java.util.List;

public abstract class NodePreprocess implements DefinedPreprocess<Node, Parent<Node>>, Preprocess {
    @Override
    public List<Node> define(Parent<Node> parent) {
        return parent.children();
    }

    @Override
    public final void preprocess(Document document) throws ParserException {
        if (document.hasRoot()) {
            this.preprocess(document.getRoot());
        }
    }

    @Override
    public final void preprocess(Node node) throws ParserException {
        this.invoke(node);
        for (Node child : node.children()) {
            this.preprocess(child);
        }
    }

    @Override
    public final void preprocess(Property property) throws ParserException {
    }
}
