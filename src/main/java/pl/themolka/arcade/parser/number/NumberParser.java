package pl.themolka.arcade.parser.number;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;

import java.util.Collections;
import java.util.Set;

public abstract class NumberParser<T extends Number> extends ElementParser<T> {
    public NumberParser() {
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("a number");
    }

    @Override
    protected ParserResult<T> parseElement(Element element, String name, String value) throws ParserException {
        try {
            return ParserResult.fine(element, name, value, this.parse(value));
        } catch (NumberFormatException cause) {
            throw this.fail(element, name, value, "Illegal number (or not a number)", cause);
        }
    }

    protected abstract T parse(String input) throws NumberFormatException;
}
