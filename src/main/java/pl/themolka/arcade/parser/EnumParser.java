package pl.themolka.arcade.parser;

import org.apache.commons.lang3.StringUtils;
import pl.themolka.arcade.dom.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Simple and easy {@link Enum} parsing.
 */
public class EnumParser<T extends Enum<T>> extends ElementParser<T> {
    private final Class<T> type;

    public EnumParser(Class<T> type) {
        this.type = Objects.requireNonNull(type, "type cannot be null");
    }

    @Override
    public Set<Object> expect() {
        Set<Object> expect = new HashSet<>();

        T[] constants = this.type.getEnumConstants();
        for (T constant : constants) {
            expect.add(toEnumValue(constant.name()));
        }

        return expect;
    }

    @Override
    public Set<Object> expectCompact() {
        return Collections.singleton(this.type.getSimpleName() + " constant");
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

    public static String toPrettyCapitalized(String input) {
        String[] words = toPrettyValue(input).split(" ");
        for (int i = 0; i < words.length; i++) {
            words[i] = words[0].length() == 1 ? words[0] : StringUtils.capitalize(words[0]);
        }

        return StringUtils.join(words, ' ');
    }

    public <E extends Enum<E>> List<Object> valueNames(Class<E> enumClass) {
        List<Object> valueNames = new ArrayList<>();
        for (E constant : enumClass.getEnumConstants()) {
            valueNames.add(toPrettyValue(constant.name()));
        }

        return valueNames;
    }
}
