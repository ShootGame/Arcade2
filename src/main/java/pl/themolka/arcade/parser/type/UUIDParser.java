package pl.themolka.arcade.parser.type;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@Produces(UUID.class)
public class UUIDParser extends ElementParser<UUID> {
    @Override
    public Set<Object> expect() {
        return Collections.singleton("an UUID");
    }

    @Override
    protected ParserResult<UUID> parseElement(Element element, String name, String value) throws ParserException {
        try {
            UUID result = this.parseUnknown(value);
            if (result != null) {
                return ParserResult.fine(element, name, value, result);
            }

            throw this.fail(element, name, value, "Unknown UUID format");
        } catch (IllegalArgumentException ex) {
            throw this.fail(element, name, value, "Illegal UUID syntax", ex);
        }
    }

    protected UUID parseUnknown(String input) throws IllegalArgumentException {
        switch (input.length()) {
            case 32: return this.parseTrimmed(input);
            case 36: return this.parseStandard(input);
            default: return null;
        }
    }

    protected UUID parseTrimmed(String input) throws IllegalArgumentException {
        return UUID.fromString(input.substring(0, 8) + "-" +
                input.substring(8, 12) + "-" +
                input.substring(12, 16) + "-" +
                input.substring(16, 20) + "-" +
                input.substring(20, 32));
    }

    protected UUID parseStandard(String input) throws IllegalArgumentException {
        return UUID.fromString(input);
    }
}
