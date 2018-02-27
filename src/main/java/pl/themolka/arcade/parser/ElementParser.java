package pl.themolka.arcade.parser;

import pl.themolka.arcade.dom.Element;

/**
 * Simple and easy {@link Element} parsing.
 */
public abstract class ElementParser<T> extends AbstractParser<T> {
    public ElementParser() {
    }

    @Override
    protected ParserResult<T> parse(Element element, String name, String value) throws ParserException {
        if (this.missingValue(value)) {
            return ParserResult.empty(element, name);
        }

        return this.parseElement(element, name, value);
    }

    protected boolean missingValue(String value) {
        return value == null;
    }

    protected abstract ParserResult<T> parseElement(Element element, String name, String value) throws ParserException;
}
