package pl.themolka.arcade.config;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;

@Produces(Ref.class)
public class RefParser extends ElementParser<Ref> {
    @Override
    public Set<Object> expect() {
        return Collections.singleton("an ID");
    }

    @Override
    protected ParserResult<Ref> parseElement(Element element, String name, String value) throws ParserException {
        return ParserResult.fine(element, name, value, Ref.of(this.validateId(value)));
    }

    private String validateId(String id) throws ParserException {
        return id;
    }
}
