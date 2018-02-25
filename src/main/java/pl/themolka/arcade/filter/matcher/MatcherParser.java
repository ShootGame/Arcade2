package pl.themolka.arcade.filter.matcher;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;

public interface MatcherParser<T extends Matcher> {
    default T parse(Node node, ParserContext context) throws ParserException {
        return node.isPrimitive() ? this.parsePrimitive(node, context)
                                  : this.parseTree(node, context);
    }

    default T parsePrimitive(Node node, ParserContext context) throws ParserException {
        // Try again as the tree type if the value is
        // empty (eg. starting tag is closing tag).
        String value = node.getValue();
        if (value == null || value.isEmpty()) {
            return this.parseTree(node, context);
        } else {
            throw new ParserException(node, "Node " + node.getName() + " is not primitive type");
        }
    }

    default T parseTree(Node node, ParserContext context) throws ParserException {
        throw new ParserException(node, "Node " + node.getName() + " is not tree type");
    }
}
