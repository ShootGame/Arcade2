package pl.themolka.arcade.portal;

import net.engio.mbassy.listener.Handler;
import org.bukkit.util.Vector;
import org.jdom2.Element;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.filter.FiltersGame;
import pl.themolka.arcade.filter.FiltersModule;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.kit.KitsGame;
import pl.themolka.arcade.kit.KitsModule;
import pl.themolka.arcade.session.PlayerMoveEvent;
import pl.themolka.arcade.spawn.SpawnsGame;
import pl.themolka.arcade.spawn.SpawnsModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PortalsGame extends GameModule {
    private final Map<String, Portal> portals = new HashMap<>();

    @Override
    public void onEnable() {
        FiltersGame filters = (FiltersGame) this.getGame().getModule(FiltersModule.class);
        KitsGame kits = (KitsGame) this.getGame().getModule(KitsModule.class);
        SpawnsGame spawns = (SpawnsGame) this.getGame().getModule(SpawnsModule.class);

        for (Element child : this.getSettings().getChildren("portal")) {
            // id
            String id = child.getAttributeValue("id");
            if (id != null) {
                id = id.trim();
            }

            if (id == null || id.isEmpty()) {
                continue;
            }

            Portal portal = XMLPortal.parse(this.getGame(), child,
                    new Portal(this.getPlugin()), filters, kits, spawns);
            if (portal != null) {
                this.portals.put(id, portal);
            }
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
}
