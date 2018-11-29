package pl.themolka.arcade.parser;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.dom.Property;

/**
 * Simple and easy {@link Property} parsing.
 */
public abstract class PropertyParser<T> extends ElementParser<T> {
    public PropertyParser() {
    }

    @Override
    protected Result<T> parseElement(Element element, String name, String value) throws ParserException {
        if (element instanceof Property) {
            return this.parseProperty((Property) element, name, value);
        }

        throw this.fail(element, name, value, "Not a property");
    }

    protected abstract Result<T> parseProperty(Property property, String name, String value) throws ParserException;
}
