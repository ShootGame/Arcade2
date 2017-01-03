package pl.themolka.arcade.xml;

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

    public static ChatColor parse(Attribute xml) {
        if (xml != null) {
            return parse(xml.getValue());
        }

        return null;
    }

    public static ChatColor parse(Attribute xml, ChatColor def) {
        ChatColor color = parse(xml);
        if (color != null) {
            return color;
        }

        return def;
    }

    public static ChatColor parse(String value) {
        return parse(value, null);
    }

    public static ChatColor parse(String value, ChatColor def) {
        if (value != null) {
            ChatColor result = ChatColor.valueOf(parseEnumValue(value));
            if (result != null) {
                return result;
            }
        }

        return def;
    }
}
