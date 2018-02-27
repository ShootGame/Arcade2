package pl.themolka.arcade.parser;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.dom.Node;

/**
 * Simple and easy {@link Node} parsing.
 */
public abstract class NodeParser<T> extends AbstractParser<T> {
    public NodeParser() {
    }

    @Override
    protected ParserResult<T> parse(Element element, String name, String value) throws ParserException {
        if (element instanceof Node) {
            return this.parseNode((Node) element, name, value);
        }

        throw this.fail(element, name, value, "Not a node");
    }

    protected ParserResult<T> parseNode(Node node, String name, String value) throws ParserException {
        if (node.isPrimitive()) {
            return this.parsePrimitive(node, name, value);
        } else if (node.isTree()) {
            return this.parseTree(node, name);
        }

        // ^ None of these - try anyway

        ParserResult<T> primitive = this.parsePrimitive(node, name, value);
        if (primitive != null) {
            return primitive;
        }

        ParserResult<T> tree = this.parseTree(node, name);
        if (tree != null) {
            return tree;
        }

        throw this.fail(node, name, value, "Parser not implemented correctly");
    }

    /**
     * Primitive <b>or trimmed</b> {@link Node} type.
     * NOTE: {@code value} can be {@code null}.
     */
    protected ParserResult<T> parsePrimitive(Node node, String name, String value) throws ParserException {
        throw this.fail(node, name, value, "Node is not tree type");
    }

    /**
     * Tree {@link Node} type.
     */
    protected ParserResult<T> parseTree(Node node, String name) throws ParserException {
        throw this.fail(node, name, "Node is not primitive type");
    }
}
