package pl.themolka.arcade.dom.preprocess;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;

import java.util.List;

public interface TreePreprocessHandler {
    default List<Node> defineNode(Node parent) {
        return parent.children();
    }

    default List<Property> defineProperty(Node parent) {
        return parent.properties();
    }

    default void invokeNode(Node node) throws PreprocessException {
    }

    default void invokeProperty(Property property) throws PreprocessException {
    }
}
