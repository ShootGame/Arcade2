package pl.themolka.arcade.parser;

import pl.themolka.arcade.dom.Element;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

// Note: This parser is unique - create its instance with the create method.
public class EnumParser<T extends Enum<T>> extends AbstractParser<T> {
    private final Class<T> type;

    private EnumParser(Class<T> type) {
        this.type = Objects.requireNonNull(type, "type cannot be null");
    }

    @Override
    public List<Object> expect() {
        return Collections.singletonList(this.type.getSimpleName());
    }

    @Override
    protected ParserResult<T> parse(Element element, String name, String value) throws ParserException {
        String input = value.toUpperCase().replace(" ", "_").replace("-", "_");

        try {
            // try the fastest way first
            return ParserResult.fine(element, name, value, Enum.valueOf(this.type, input));
        } catch (IllegalArgumentException ex) {
            // try case-insensitive
            for (T constant : this.type.getEnumConstants()) {
                if (constant.name().equalsIgnoreCase(input)) {
                    return ParserResult.fine(element, name, value, constant);
                }
            }

            // try contains
            for (T constant : this.type.getEnumConstants()) {
                if (constant.name().toUpperCase().contains(input)) {
                    return ParserResult.fine(element, name, value, constant);
                }
            }
        }

        throw this.fail(element, name, value, "Unknown enum property");
    }

    public Class<T> getType() {
        return this.type;
    }

    //
    // Instancing
    //

    public static <T extends Enum<T>> EnumParser<T> create(Class<T> enumClass) {
        return new EnumParser<>(enumClass);
    }
}