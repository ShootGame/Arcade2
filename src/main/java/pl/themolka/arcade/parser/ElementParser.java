package pl.themolka.arcade.parser;

import pl.themolka.arcade.dom.Element;

/**
 * Simple and easy {@link Element} parsing.
 */
public abstract class ElementParser<T> extends AbstractParser<T> {
    public ElementParser() {
    }

    @Override
    protected Result<T> parse(Element element, String name, String value) throws ParserException {
        if (this.missingValue(value)) {
            return Result.empty(element, name);
        }

        return this.parseElement(element, name, value);
    }

    protected boolean missingValue(String value) {
        return value == null;
    }

    protected abstract Result<T> parseElement(Element element, String name, String value) throws ParserException;
}
