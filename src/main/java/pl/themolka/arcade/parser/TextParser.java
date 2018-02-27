package pl.themolka.arcade.parser;

import pl.themolka.arcade.dom.Element;

import java.util.Collections;
import java.util.List;

/**
 * This the default parser behavior. We are using {@link ElementParser} for it,
 * so the {@code value} parameter is never {@code null}.
 */
public class TextParser extends ElementParser<String> {
    @Override
    public List<Object> expect() {
        return Collections.singletonList("a text");
    }

    @Override
    protected ParserResult<String> parseElement(Element element, String name, String value) throws ParserException {
        return ParserResult.fine(element, name, value, value);
    }
}
