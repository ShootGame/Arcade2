/*
 * Copyright 2018 Aleksander Jagiełło
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.themolka.arcade.portal;

import net.engio.mbassy.listener.Handler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.IGameModuleConfig;
import pl.themolka.arcade.session.PlayerJoinEvent;
import pl.themolka.arcade.session.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PortalsGame extends GameModule {
    private final Map<String, Portal> portals = new HashMap<>();

    protected PortalsGame(Game game, IGameConfig.Library library, Config config) {
        for (Portal.Config portal : config.portals().get()) {
            this.portals.put(portal.id(), library.getOrDefine(game, portal));
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
        if (!event.isCanceled()) {
            this.detectPortal(event.getGamePlayer(), event.getTo().toVector());
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void detectPortal(PlayerTeleportEvent event) {
        GamePlayer player = this.getGame().resolve(event.getPlayer());
        if (player != null) {
            this.detectPortal(player, event.getTo().toVector());
        }
    }

    // See pl.themolka.arcade.capture.point.PointCapture#onPlayerInitialSpawn
    @Handler(priority = Priority.LOW)
    public void detectPortal(PlayerJoinEvent event) {
        GamePlayer player = event.getGamePlayer();
        this.detectPortal(player, player.getBukkit().getLocation().toVector());
    }

    private Portal detectPortal(GamePlayer player, Vector at) {
        Portal portal = this.getPortal(at);
        if (portal == null) {
            return null;
        }

        if (portal.canTeleport(player)) {
            portal.teleport(player);
            return portal;
        }

        return null;
    }

    public interface Config extends IGameModuleConfig<PortalsGame> {
        Ref<Set<Portal.Config>> portals();

        @Override
        default PortalsGame create(Game game, Library library) {
            return new PortalsGame(game, library, this);
        }
    }
}
