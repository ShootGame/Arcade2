package pl.themolka.arcade.parser.number;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.AbstractParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;

import java.util.Collections;
import java.util.List;

public abstract class NumberParser<T extends Number> extends AbstractParser<T> {
    @Override
    public List<Object> expect() {
        return Collections.singletonList("a number");
    }

    @Override
    protected ParserResult<T> parse(Element element, String name, String value) throws ParserException {
        try {
            return ParserResult.fine(element, name, value, this.parse(value));
        } catch (NumberFormatException cause) {
            throw this.fail(element, name, value, cause);
        }
    }

    protected abstract T parse(String input) throws NumberFormatException;
}
