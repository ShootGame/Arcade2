package pl.themolka.arcade.portal;

import org.bukkit.entity.Player;
import org.jdom2.Element;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.FiltersGame;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.kit.Kit;
import pl.themolka.arcade.kit.KitsGame;
import pl.themolka.arcade.region.Region;
import pl.themolka.arcade.region.XMLRegion;
import pl.themolka.arcade.spawn.Direction;
import pl.themolka.arcade.spawn.SmoothSpawnAgent;
import pl.themolka.arcade.spawn.Spawn;
import pl.themolka.arcade.spawn.SpawnAgent;
import pl.themolka.arcade.spawn.SpawnApply;
import pl.themolka.arcade.spawn.SpawnsGame;
import pl.themolka.arcade.xml.XMLParser;

public class XMLPortal extends XMLParser {
    private static final Direction defaultYaw = Direction.ENTITY;
    private static final Direction defaultPitch = Direction.ENTITY;

    public static Portal parse(Game game, Element xml, Portal portal,
                               FiltersGame filters, KitsGame kits, SpawnsGame spawns) {
        // smooth
        boolean smooth = parseBoolean(xml.getAttributeValue("smooth"), false);

        // destination
        if (spawns != null) {
            SpawnApply destination = SpawnApply.parse(xml.getAttributeValue("destination"), spawns, new SpawnApply.AgentCreator() {
                @Override
                public SpawnAgent createAgent(Spawn spawn, GamePlayer player, Player bukkit) {
                    Direction yaw = DirectionValues.of(xml.getAttributeValue("yaw"), defaultYaw);
                    Direction pitch = DirectionValues.of(xml.getAttributeValue("pitch"), defaultPitch);

                    if (smooth) {
                        return SmoothSpawnAgent.create(spawn, bukkit, yaw, pitch);
                    } else {
                        return SpawnAgent.create(spawn, bukkit, yaw, pitch);
                    }
                }
            });

            if (destination == null) {
                return null; // destination is required!
            }
            portal.setDestination(destination);
        }

        // filter
        if (filters != null) {
            Filter filter = filters.filterOrDefault(xml.getAttributeValue("filter"), null);
            if (filter != null) {
                portal.setFilter(filter);
            }
        }

        // kit
        if (kits != null) {
            Kit kit = kits.getKit(xml.getAttributeValue("kit"));
            if (kit != null) {
                portal.setKit(kit);
            }
        }

        // region
        Region region = XMLRegion.parseUnion(game, xml);
        if (region == null) {
            return null; // region is required!
        }
        portal.setRegion(region);

        return portal;
    }

    private enum DirectionValues {
        CONSTANT(Direction.CONSTANT),
        ENTITY(Direction.ENTITY),
        RELATIVE(Direction.RELATIVE),
        TRANSLATE(Direction.TRANSLATE),
        ;

        final Direction direction;

        DirectionValues(Direction direction) {
            this.direction = direction;
        }

        static Direction of(String value, Direction def) {
            if (value != null) {
                try {
                    return valueOf(XMLParser.parseEnumValue(value)).direction;
                } catch (IllegalArgumentException ignored) {
                }
            }

            return def;
        }
    }
}
