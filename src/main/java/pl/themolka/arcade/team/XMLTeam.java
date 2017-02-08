package pl.themolka.arcade.team;

import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.util.Color;
import pl.themolka.arcade.xml.XMLChatColor;
import pl.themolka.arcade.xml.XMLDyeColor;
import pl.themolka.arcade.xml.XMLParser;

import java.util.ArrayList;
import java.util.List;

public class XMLTeam extends XMLParser {
    public static Team parse(Element xml, ArcadePlugin plugin) {
        String name = parseName(xml);
        ChatColor color = parseColor(xml);
        DyeColor dye = parseDyeColor(xml);
        boolean friendly = parseFriendlyFire(xml);
        int min = parseMinPlayers(xml);
        int slots = parseSlots(xml);
        int overfill = parseMaxPlayers(xml);

        if (color == null) {
            color = Color.randomChat();
        }

        if (dye == null) {
            dye = Color.ofChat(color).toDye();
        }

        if (slots == 0) {
            slots = Integer.MAX_VALUE;
        }

        if (overfill == 0) {
            if (slots == Integer.MAX_VALUE) {
                overfill = slots;
            } else {
                // overfill = slots + 25%
                overfill = slots + (slots / 4);
            }
        }

        return new TeamBuilder(plugin, parseId(xml))
                .color(color)
                .dyeColor(dye)
                .friendlyFire(friendly)
                .maxPlayers(overfill)
                .minPlayers(min)
                .name(name)
                .slots(slots)
                .spawns(parseSpawns(xml))
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
        Attribute attribute = xml.getAttribute("overfill");
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
            String name = attribute.getValue();
            if (name.length() <= Team.NAME_MAX_LENGTH) {
                return name;
            }
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

    public static List<TeamSpawn> parseSpawns(Element xml) {
        List<TeamSpawn> spawns = new ArrayList<>();

        Element spawnsElement = xml.getChild("spawns");
        if (spawnsElement == null) {
            return spawns;
        }

        for (Element element : spawnsElement.getChildren()) {
            TeamSpawn spawn = null;
            switch (element.getName().toLowerCase()) {
                case "region":

                    break;
            }

            if (spawn != null) {
                spawns.add(spawn);
            }
        }

        return spawns;
    }
}
