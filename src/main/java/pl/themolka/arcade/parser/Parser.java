package pl.themolka.arcade.parser;

import pl.themolka.arcade.dom.Element;

import java.util.List;

public interface Parser<T> {
    List<Object> expect();

    default ParserResult<T> parse(Element element) {
        return element != null ? this.parseWithDefinition(element, element.getName(), element.getValue())
                               : (ParserResult<T>) ParserResult.empty();
    }

    default ParserResult<T> parseWithDefinition(String name, String value) {
        return this.parseWithDefinition(null, name, value);
    }

    ParserResult<T> parseWithDefinition(Element element, String name, String value);

    default ParserResult<T> parseWithName(Element element, String name) {
        return this.parseWithDefinition(element, name, element != null ? element.getValue() : null);
    }

    default ParserResult<T> parseWithValue(Element element, String value) {
        return element != null ? this.parseWithDefinition(element, element.getName(), value)
                               : (ParserResult<T>) ParserResult.empty();
    }
}
