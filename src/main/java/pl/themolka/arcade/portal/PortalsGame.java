package pl.themolka.arcade.portal;

import net.engio.mbassy.listener.Handler;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jdom2.Element;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.FiltersGame;
import pl.themolka.arcade.filter.FiltersModule;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.kit.Kit;
import pl.themolka.arcade.kit.KitsGame;
import pl.themolka.arcade.kit.KitsModule;
import pl.themolka.arcade.region.Region;
import pl.themolka.arcade.region.XMLRegion;
import pl.themolka.arcade.session.PlayerMoveEvent;
import pl.themolka.arcade.spawn.Direction;
import pl.themolka.arcade.spawn.Spawn;
import pl.themolka.arcade.spawn.SpawnAgent;
import pl.themolka.arcade.spawn.SpawnApply;
import pl.themolka.arcade.spawn.SpawnsGame;
import pl.themolka.arcade.spawn.SpawnsModule;
import pl.themolka.arcade.xml.XMLParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PortalsGame extends GameModule {
    private final Map<String, Portal> portals = new HashMap<>();

    @Override
    public void onEnable() {
        FiltersGame filtersGame = (FiltersGame) this.getGame().getModule(FiltersModule.class);
        KitsGame kitsGame = (KitsGame) this.getGame().getModule(KitsModule.class);
        SpawnsGame spawnsGame = (SpawnsGame) this.getGame().getModule(SpawnsModule.class);

        for (Element child : this.getSettings().getChildren("portal")) {
            Portal portal = new Portal(this);

            // id
            String id = child.getAttributeValue("id");
            if (id != null) {
                id = id.trim();
            }

            if (id == null || id.isEmpty()) {
                continue;
            }

            // destination
            String destinationId = child.getAttributeValue("destination");
            if (destinationId != null && spawnsGame != null) {
                SpawnApply destination = SpawnApply.parse(destinationId, spawnsGame, new SpawnApply.AgentCreator() {
                    @Override
                    public SpawnAgent createAgent(Spawn spawn, GamePlayer player, Player bukkit) {
                        Direction direction = DirectionValues.of(child.getAttributeValue("direction"),
                                                                 Direction.ENTITY);
                        return SpawnAgent.create(spawn, bukkit, direction);
                    }
                });

                if (destination == null) {
                    continue; // destination is required!
                }
                portal.setDestination(destination);
            }

            // filter
            String filterId = child.getAttributeValue("filter");
            if (filterId != null && filtersGame != null) {
                Filter filter = filtersGame.getFilter(filterId.trim());

                if (filter != null) {
                    portal.setFilter(filter);
                }
            }

            // kit
            String kitId = child.getAttributeValue("kit");
            if (kitId != null && kitsGame != null) {
                Kit kit = kitsGame.getKit(kitId.trim());

                if (kit != null) {
                    portal.setKit(kit);
                }
            }

            // region
            Region region = XMLRegion.parseUnion(this.getGame().getMap(), child);
            if (region == null) {
                continue; // region is required!
            }
            portal.setRegion(region);

            this.portals.put(id, portal);
        }
    }

    public void addPortal(String id, Portal portal) {
        this.portals.put(id, portal);
    }

    public Portal getPortal(String id) {
        return this.portals.get(id);
    }

    public Portal getPortal(Vector at) {
        for (Portal portal : this.portals.values()) {
            if (portal.getFieldStrategy().regionContains(portal, at)) {
                return portal;
            }
        }

        return null;
    }

    public Set<String> getPortalIds() {
        return this.portals.keySet();
    }

    public List<Portal> getPortals() {
        return new ArrayList<>(this.portals.values());
    }

    public boolean removePortal(String id) {
        return this.portals.remove(id) != null;
    }

    @Handler(priority = Priority.LOW)
    public void detectPortal(PlayerMoveEvent event) {
        if (event.isCanceled()) {
            return;
        }

        GamePlayer player = event.getGamePlayer();
        Portal portal = this.getPortal(event.getTo().toVector());

        if (portal != null && portal.canTeleport(player)) {
            portal.teleport(player);
        }
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
