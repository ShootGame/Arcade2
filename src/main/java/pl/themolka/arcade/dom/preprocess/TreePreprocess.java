package pl.themolka.arcade.dom.preprocess;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Parent;
import pl.themolka.arcade.dom.Property;

import java.util.List;
import java.util.Objects;

public class TreePreprocess extends NodePreprocess
                            implements TreePreprocessHandler {
    private final TreePreprocessHandler handler;

    public TreePreprocess(TreePreprocessHandler handler) {
        this.handler = Objects.requireNonNull(handler, "handler cannot be null");
    }

    @Override
    public final List<Node> define(Parent<Node> parent) {
        return parent.children();
    }

    @Override
    public final void invoke(Node node) throws PreprocessException {
        List<Node> nodeDefinition = this.defineNode(node);
        if (nodeDefinition != null) {
            for (Node definedChild : nodeDefinition) {
                this.invokeNode(definedChild);
                for (Property property : definedChild.properties()) {
                    this.invokeProperty(property);
                }
            }
        }

        for (Node child : this.define(node)) {
            this.preprocess(child);
        }
    }

    @Override
    public void invokeNode(Node node) throws PreprocessException {
        this.handler.invokeNode(node);
    }

    @Override
    public void invokeProperty(Property property) throws PreprocessException {
        this.handler.invokeProperty(property);
    }

    public TreePreprocessHandler getHandler() {
        return this.handler;
    }
}
