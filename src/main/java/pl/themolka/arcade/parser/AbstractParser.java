package pl.themolka.arcade.parser;

import org.apache.commons.lang3.StringUtils;
import pl.themolka.arcade.dom.Element;

import java.util.List;

public abstract class AbstractParser<T> implements Parser<T> {
    @Override
    public ParserResult<T> parseWithDefinition(Element element, String name, String value) {
        if (element == null) {
            element = Element.empty();
        }
        if (name == null) {
            name = element.getName();
        }
        if (value == null) {
            value = element.getValue();
        }

        try {
            String normalizedName = this.normalizeInput(name);
            String normalizedValue = this.normalizeInput(value);

            if (normalizedName != null && normalizedValue != null) {
                return this.parse(element, normalizedName, normalizedValue);
            }
        } catch (ParserException cause) {
            return (ParserResult<T>) ParserResult.fail(cause, name, value);
        }

        return (ParserResult<T>) ParserResult.empty(element, name, value);
    }

    private String normalizeInput(String input) {
        if (input != null) {
            input = input.trim();
            if (!input.isEmpty()) {
                return input;
            }
        }

        return null;
    }

    //
    // Abstract Methods
    //

    @Override
    public abstract List<Object> expect();

    protected abstract ParserResult<T> parse(Element element, String name, String value) throws ParserException;

    //
    // Return Methods
    //

    protected ParserException fail(Element element, String name, String value) {
        return this.fail(element, name, value, null, null);
    }

    protected ParserException fail(Element element, String name, String value, String fail) {
        return this.fail(element, name, value, fail, null);
    }

    protected ParserException fail(Element element, String name, String value, Throwable cause) {
        return this.fail(element, name, value, null, cause);
    }

    protected ParserException fail(Element element, String name, String value, String fail, Throwable cause) {
        List<Object> expect = this.expect();
        String expected = expect != null && !expect.isEmpty() ? " (" + StringUtils.join(expect, ", ") + " expected)" : "";

        return new ParserException(element, "Invalid input value \"" + value + "\" in \"" +
                name + "\"" + (fail != null ? " " + fail : "") + expected, cause);
    }
}
