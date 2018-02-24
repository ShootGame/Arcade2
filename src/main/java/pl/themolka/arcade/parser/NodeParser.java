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

    protected abstract ParserResult<T> parse(Node node, String name, String value);
}
