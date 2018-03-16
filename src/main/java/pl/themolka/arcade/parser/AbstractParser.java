package pl.themolka.arcade.parser;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.dom.EmptyElement;

import java.util.Set;

/**
 * Base class for all parsers with simple exception handling.
 *
 * <ul>
 *   <li>The default behavior of parsers is {@link TextParser}.</li>
 *   <li>{@link Enum}s can easily be parsed using the {@link EnumParser}.</li>
 * </ul>
 *
 * Normally, this class is used as a base class for all DOM type parsers. All
 * parsers should inherit respective DOM type parser. These are as follows:
 *
 * <ul>
 *   <li><b>{@link ElementParser}</b> for non-null key/value pair which can
 *   parse all {@link Element}s.</li>
 *   <li><b>{@link NodeParser}</b> for primitive or tree type
 *   {@link pl.themolka.arcade.dom.Node}s.</li>
 *   <li><b>{@link PropertyParser}</b> for non-null
 *   {@link pl.themolka.arcade.dom.Property}</li>
 * </ul>
 */
public abstract class AbstractParser<T> extends ParserValidation
                                        implements Parser<T> {
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

            if (normalizedName != null) {
                return this.parse(element, normalizedName, normalizedValue);
            }
        } catch (ParserException cause) {
            return ParserResult.fail(cause, name, value);
        }

        return ParserResult.empty(element, name);
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
    public abstract Set<Object> expect();

    protected abstract ParserResult<T> parse(Element element, String name, String value) throws ParserException;

    protected String normalizeName(String name) throws ParserException {
        return this.normalizeInput(name);
    }

    protected String normalizeValue(String value) throws ParserException {
        return this.normalizeInput(value);
    }
}
