package pl.themolka.arcade.xml.parser;

import org.bukkit.ChatColor;
import org.jdom2.Attribute;
import org.jdom2.Element;

public class XMLChatColor extends XMLParser {
    public static final String ATTRIBUTE_COLOR = "color";

    public static ChatColor parse(Element xml) {
        return parse(xml.getAttribute(ATTRIBUTE_COLOR));
    }

    public static ChatColor parse(Element xml, ChatColor def) {
        ChatColor color = parse(xml);
        if (color != null) {
            return color;
        }

        return def;
    }

    public static org.bukkit.ChatColor parse(Attribute xml) {
        return ChatColor.valueOf(parseEnumValue(xml.getValue()));
    }

    public static ChatColor parse(Attribute xml, ChatColor def) {
        ChatColor color = parse(xml);
        if (color != null) {
            return color;
        }

        return def;
    }
}
