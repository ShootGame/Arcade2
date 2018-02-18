package pl.themolka.arcade.filter.matcher;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.ParserException;

public interface MatcherParser<T extends Matcher> {
    default T parse(Node node) throws ParserException {
        if (node.isPrimitive()) {
            return this.parsePrimitive(node);
        } else if (node.isTree()) {
            return this.parseTree(node);
        }

        return null;
    }

    default T parsePrimitive(Node node) throws ParserException {
        // Try again as the tree type if the value is
        // empty (eg. starting tag is closing tag).
        String value = node.getValue();
        if (value.isEmpty()) {
            return this.parseTree(node);
        } else {
            throw new ParserException(node, "Node " + node.getName() + " is not primitive");
        }
    }

    default T parseTree(Node node) throws ParserException {
        throw new ParserException(node, "Node " + node.getName() + " is not tree");
    }
}
