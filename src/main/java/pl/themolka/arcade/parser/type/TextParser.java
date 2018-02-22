package pl.themolka.arcade.parser.type;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.AbstractParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;

import java.util.Collections;
import java.util.List;

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
