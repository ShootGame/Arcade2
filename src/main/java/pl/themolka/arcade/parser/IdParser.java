package pl.themolka.arcade.parser;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Property;

import java.util.Collections;
import java.util.Set;

@Produces(String.class)
@Silent
public class IdParser extends PropertyParser<String> {
    @Override
    public Set<Object> expect() {
        return Collections.singleton("a unique ID");
    }

    @Override
    protected ParserResult<String> parseProperty(Property property, String name, String value) throws ParserException {
        if (!this.validId(value)) {
            throw this.fail(property, name, value, "Invalid ID syntax");
        }

        return ParserResult.fine(property, name, value, value);
    }

    private boolean validId(String id) throws ParserException {
        return Ref.ID_PATTERN.matcher(id).matches();
    }
}
