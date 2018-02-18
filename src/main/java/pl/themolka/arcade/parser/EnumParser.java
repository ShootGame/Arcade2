package pl.themolka.arcade.parser;

import pl.themolka.arcade.dom.Element;

public class EnumParser<R extends Enum<R>> implements ElementParser<R> {
    private final Class<R> type;

    public EnumParser(Class<R> type) {
        this.type = type;
    }

    @Override
    public R parse(Element element, String value) throws ParserException {
        Class<R> type = this.getType();

        String input = value.toUpperCase().replace(" ", "_").replace("-", "_");

        try {
            // try the fastest way first
            return Enum.valueOf(type, input);
        } catch (IllegalArgumentException ex) {
            // try case-insensitive
            for (R constant : type.getEnumConstants()) {
                if (constant.name().equalsIgnoreCase(input)) {
                    return constant;
                }
            }

            // try contains
            for (R constant : type.getEnumConstants()) {
                if (constant.name().toUpperCase().contains(input)) {
                    return constant;
                }
            }
        }

        return this.exception(element, value, type.getSimpleName());
    }

    public Class<R> getType() {
        return this.type;
    }
}
