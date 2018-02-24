package pl.themolka.arcade.parser;

import pl.themolka.arcade.dom.Element;

import java.util.Collections;
import java.util.List;

// This the default parser behavior.
public class TextParser extends AbstractParser<String> {
    @Override
    public List<Object> expect() {
        return Collections.singletonList("a text");
    }

    @Override
    protected ParserResult<String> parse(Element element, String name, String value) throws ParserException {
        return ParserResult.fine(element, name, value, value);
    }
}
