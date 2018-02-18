package pl.themolka.arcade.parser;

import pl.themolka.arcade.dom.Element;

public class TextParser implements ElementParser<String> {
    @Override
    public String parse(Element element, String value) throws ParserException {
        return value;
    }
}
