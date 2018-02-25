package pl.themolka.arcade.parser;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.dom.Node;

public abstract class NodeParser<T> extends AbstractParser<T> {
    public NodeParser() {
    }

    @Override
    protected ParserResult<T> parse(Element element, String name, String value) throws ParserException {
        if (element instanceof Node) {
            return this.parse((Node) element, name, value);
        }

        throw this.fail(element, name, value, "Not a node");
    }

    protected ParserResult<T> parse(Node node, String name, String value) throws ParserException {
        if (node.isPrimitive()) {
            return this.parsePrimitive(node, name, value);
        } else if (node.isTree()) {
            return this.parseTree(node, name, value);
        }

        ParserResult<T> primitive = this.parsePrimitive(node, name, value);
        if (primitive != null) {
            return primitive;
        }

        ParserResult<T> tree = this.parseTree(node, name, value);
        if (tree != null) {
            return tree;
        }

        throw this.fail(node, name, value, "Parser not implemented correctly");
    }

    protected ParserResult<T> parsePrimitive(Node node, String name, String value) throws ParserException {
        throw this.fail(node, name, value, "Node is not primitive type");
    }

    protected ParserResult<T> parseTree(Node node, String name, String value) throws ParserException {
        throw this.fail(node, name, value, "Node is not tree type");
    }
}
