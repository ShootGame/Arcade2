package pl.themolka.arcade.parser.type;

import org.bukkit.ChatColor;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.List;

@Produces(String.class)
public class MessageParser extends ElementParser<String> {
    public static final char COLOR_CODE = '`';

    @Override
    public List<Object> expect() {
        return Collections.singletonList("a colorable text");
    }

    @Override
    protected ParserResult<String> parseElement(Element element, String name, String value) throws ParserException {
        return ParserResult.fine(element, name, value, this.color(value) + ChatColor.RESET);
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes(COLOR_CODE, text);
    }
}
