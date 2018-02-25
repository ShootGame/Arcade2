package pl.themolka.arcade.parser;

import org.apache.commons.lang3.StringUtils;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.dom.EmptyElement;

import java.util.List;

public abstract class AbstractParser<T> implements Parser<T> {
    public AbstractParser() {
    }

    @Override
    public ParserResult<T> parseWithDefinition(Element element, String name, String value) {
        if (element == null) {
            element = EmptyElement.empty();
        } else if (name == null) {
            name = element.getName();
        }

        if (value == null) {
            value = element.getValue();
        }

        try {
            String normalizedName = this.normalizeName(name);
            String normalizedValue = this.normalizeValue(value);

            if (normalizedName != null && normalizedValue != null) {
                return this.parse(element, normalizedName, normalizedValue);
            }
        } catch (ParserException cause) {
            return ParserResult.fail(cause, name, value);
        }

        return ParserResult.empty(element, name, value);
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

    protected String normalizeName(String name) throws ParserException {
        return this.normalizeInput(name);
    }

    protected String normalizeValue(String value) throws ParserException {
        return this.normalizeInput(value);
    }

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

        return new ParserException(element, "Invalid value \"" + value + "\" in \"" +
                name + "\"" + (fail != null ? ": " + fail : "") + expected, cause);
    }
}
