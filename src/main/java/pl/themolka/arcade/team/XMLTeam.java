package pl.themolka.arcade.team;

import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.PlayerApplicable;
import pl.themolka.arcade.map.ArcadeMap;
import pl.themolka.arcade.util.Color;
import pl.themolka.arcade.xml.XMLChatColor;
import pl.themolka.arcade.xml.XMLDyeColor;
import pl.themolka.arcade.xml.XMLParser;

import java.util.ArrayList;
import java.util.List;

public class XMLTeam extends XMLParser {
    public static Team parse(ArcadeMap map, Element xml, ArcadePlugin plugin) {
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

        Team team = new TeamBuilder(plugin, parseId(xml))
                .color(color)
                .dyeColor(dye)
                .friendlyFire(friendly)
                .maxPlayers(overfill)
                .minPlayers(min)
                .name(name)
                .slots(slots)
                .build();

        for (Element applyItem : xml.getChildren("apply")) {
            ApplyResultEntry entry = parseApply(map, applyItem);
            for (TeamApplyEvent event : entry.getEvents()) {
                team.addApplyContent(event, entry.getApplicable());
            }
        }

        return team;
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
        return XMLParser.parseBoolean(xml.getAttributeValue("friendly-fire"), false);
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

    //
    // Applicable
    //

    public static ApplyResultEntry parseApply(ArcadeMap map, Element xml) {
        TeamApplyEvent[] events = TeamApplyEvent.ofCodeMany(xml.getAttributes());
        List<PlayerApplicable> applicableList = new ArrayList<>();

        for (Element apply : xml.getChildren()) {
            PlayerApplicable result = parseApplyItem(map, apply);
            if (result != null) {
                applicableList.add(result);
            }
        }

        return new ApplyResultEntry(new PlayerApplicable() {
            @Override
            public void apply(GamePlayer player) {
                for (PlayerApplicable applicable : applicableList) {
                    applicable.apply(player);
                }
            }
        }, events);
    }

    private static PlayerApplicable parseApplyItem(ArcadeMap map, Element xml) {
        switch (xml.getName().toLowerCase()) {
            case "kit":
                return null; // TODO kits
            case "spawns":
                return TeamSpawnsApply.parse(map, xml);
            default:
                return null;
        }
    }

    public static class ApplyResultEntry {
        private final PlayerApplicable applicable;
        private final TeamApplyEvent[] events;

        private ApplyResultEntry(PlayerApplicable applicable, TeamApplyEvent[] events) {
            this.applicable = applicable;

            if (events != null && events.length != 0) {
                this.events = events;
            } else {
                this.events = TeamApplyEvent.values();
            }
        }

        public PlayerApplicable getApplicable() {
            return this.applicable;
        }

        public TeamApplyEvent[] getEvents() {
            return this.events;
        }
    }
}
