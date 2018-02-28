package pl.themolka.arcade.parser;

import pl.themolka.arcade.dom.Document;
import pl.themolka.arcade.dom.Element;

import java.util.Set;

/**
 * Something that can or cannot parse T.
 * <b>The result cannot be {@code null}!</b>
 */
public interface Parser<T> {
    Set<Object> expect();

    default ParserResult<T> parse(Document document) {
        return document != null && document.hasRoot() ? this.parse(document.getRoot())
                                                      : ParserResult.empty();
    }

    default ParserResult<T> parse(Element element) {
        return element != null ? this.parseWithDefinition(element, element.getName(), element.getValue())
                               : ParserResult.empty();
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
                               : ParserResult.empty();
    }
}
