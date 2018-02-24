package pl.themolka.arcade.parser;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.dom.Property;

public abstract class PropertyParser<T> extends AbstractParser<T> {
    public PropertyParser() {
    }

    @Override
    protected ParserResult<T> parse(Element element, String name, String value) throws ParserException {
        if (element instanceof Property) {
            return this.parse((Property) element, name, value);
        }

        throw this.fail(element, name, value, "Not a property");
    }

    protected abstract ParserResult<T> parse(Property property, String name, String value);
}
