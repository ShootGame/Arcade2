package pl.themolka.arcade.parser;

import pl.themolka.arcade.dom.Element;

public interface ElementParser<R> extends Parser<Element, R> {
    @Override
    default R parse(Element element) throws ParserException {
        return this.parse(element, this.normalizeInput(element, element.getValue()));
    }

    default String normalizeInput(Element element, String input) throws ParserException {
        if (input != null) {
            input = input.trim();
            if (!input.isEmpty()) {
                return input;
            }
        }

        throw new ParserException(element, "No value defined (or empty)");
    }

    R parse(Element element, String value) throws ParserException;

    // protected?
    default R exception(Element element, String input, String expected) throws ParserException {
        return this.exception(element, input, expected, null);
    }

    // protected?
    default R exception(Element element, String input, String expected, Throwable cause) throws ParserException {
        throw new ParserException(element, "Invalid input value \"" + input + "\" (" + expected + " expected)", cause);
    }
}
