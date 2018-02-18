package pl.themolka.arcade.parser;

import org.bukkit.ChatColor;
import pl.themolka.arcade.dom.Element;

public class MessageParser implements ElementParser<String> {
    public static final char COLOR_CODE = '`';

    @Override
    public String parse(Element element, String value) throws ParserException {
        return ChatColor.translateAlternateColorCodes(COLOR_CODE, value) + ChatColor.RESET;
    }
}
