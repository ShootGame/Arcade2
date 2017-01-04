package pl.themolka.arcade.team;

import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.xml.XMLChatColor;
import pl.themolka.arcade.xml.XMLDyeColor;
import pl.themolka.arcade.xml.XMLParser;

public class XMLTeam extends XMLParser {
    public static Team parse(Element xml, ArcadePlugin plugin) {
        return new TeamBuilder(plugin, parseId(xml))
                .color(parseColor(xml))
                .dyeColor(parseDyeColor(xml))
                .friendlyFire(parseFriendlyFire(xml))
                .maxPlayers(parseMaxPlayers(xml))
                .minPlayers(parseMinPlayers(xml))
                .name(parseName(xml))
                .slots(parseSlots(xml))
                .build();
    }

    public static String parseId(Element xml) {
        Attribute attribute = xml.getAttribute("id");
        if (attribute != null) {
            return attribute.getValue();
        }

        return RandomStringUtils.randomAlphanumeric(5);
    }

    public static ChatColor parseColor(Element xml) {
        Attribute attribute = xml.getAttribute("color");
        if (attribute != null) {
            return XMLChatColor.parse(attribute);
        }

        return null;
    }

    public static DyeColor parseDyeColor(Element xml) {
        Attribute attribute = xml.getAttribute("dye-color");
        if (attribute != null) {
            return XMLDyeColor.parse(attribute);
        }

        return null;
    }

    public static boolean parseFriendlyFire(Element xml) {
        Attribute attribute = xml.getAttribute("friendly-fire");
        if (attribute != null) {
            try {
                return attribute.getBooleanValue();
            } catch (DataConversionException ignored) {
            }
        }

        return false;
    }

    public static int parseMaxPlayers(Element xml) {
        Attribute attribute = xml.getAttribute("max-players");
        if (attribute != null) {
            try {
                return attribute.getIntValue();
            } catch (DataConversionException ignored) {
            }
        }

        return 0;
    }

    public static int parseMinPlayers(Element xml) {
        Attribute attribute = xml.getAttribute("min-players");
        if (attribute != null) {
            try {
                return attribute.getIntValue();
            } catch (DataConversionException ignored) {
            }
        }

        return 0;
    }

    public static String parseName(Element xml) {
        Attribute attribute = xml.getAttribute("name");
        if (attribute != null) {
            return attribute.getValue();
        }

        return RandomStringUtils.randomAlphanumeric(5);
    }

    public static int parseSlots(Element xml) {
        Attribute attribute = xml.getAttribute("slots");
        if (attribute != null) {
            try {
                return attribute.getIntValue();
            } catch (DataConversionException ignored) {
            }
        }

        return 0;
    }
}
