package pl.themolka.arcade.parser;

import pl.themolka.arcade.dom.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Simple and easy {@link Enum} parsing.
 */
public class EnumParser<T extends Enum<T>> extends ElementParser<T> {
    private final Class<T> type;

    public EnumParser(Class<T> type) {
        this.type = Objects.requireNonNull(type, "type cannot be null");
    }

    @Override
    public List<Object> expect() {
        List<Object> expect = new ArrayList<>();

        T[] constants = this.type.getEnumConstants();
        for (T constant : constants) {
            expect.add(toPrettyValue(toPrettyValue(constant.name())));
        }

        return expect;
    }

    @Override
    public List<Object> expectCompact() {
        return Collections.singletonList(this.type.getSimpleName() + " constant");
    }

    @Override
    protected ParserResult<T> parseElement(Element element, String name, String value) throws ParserException {
        String input = toEnumValue(value);

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

        throw this.fail(element, name, value, "Unknown " + this.type.getSimpleName() + " property");
    }

    public Class<T> getType() {
        return this.type;
    }

    public static String toEnumValue(String input) {
        return input.toUpperCase().replace(" ", "_")
                                  .replace("-", "_");
    }

    public static String toPrettyValue(String input) {
        return input.toLowerCase().replace("_", " ");
    }

    public <T extends Enum<T>> List<Object> valueNames(Class<T> enumClass) {
        List<Object> valueNames = new ArrayList<>();
        for (T constant : enumClass.getEnumConstants()) {
            valueNames.add(toPrettyValue(constant.name()));
        }

        return valueNames;
    }
}
