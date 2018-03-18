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
        UUID result;
        try {
            if (value.length() == 32) {
                // trimmed format
                result = UUID.fromString(value.substring(0, 8) + "-" +
                                         value.substring(8, 12) + "-" +
                                         value.substring(12, 16) + "-" +
                                         value.substring(16, 20) + "-" +
                                         value.substring(20, 32));
            } else if (value.length() == 36) {
                // standard format
                result = UUID.fromString(value);
            } else {
                throw this.fail(element, name, value, "Unknown UUID format");
            }
        } catch (IllegalArgumentException ex) {
            throw this.fail(element, name, value, "Illegal UUID syntax", ex);
        }

        return ParserResult.fine(element, name, value, result);
    }
}
