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

package pl.themolka.arcade.session;

import com.destroystokyo.paper.event.player.PlayerInitialSpawnEvent;
import net.engio.mbassy.listener.Handler;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import pl.themolka.arcade.event.Event;
import pl.themolka.arcade.event.PluginReadyEvent;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.service.Service;
import pl.themolka.arcade.service.ServiceId;

import java.util.ArrayList;
import java.util.List;

@ServiceId("Sessions")
public class SessionsService extends Service {
    private void publish(Event event) {
        this.getPlugin().getEventBus().publish(event);
    }

    private void replaceDrops(List<ItemStack> source, List<ItemStack> drops) {
        source.clear();
        source.addAll(drops);
    }

    //
    // Sessions
    //

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInitialSpawn(PlayerInitialSpawnEvent event) {
        Game game = this.getPlugin().getGames().getCurrentGame();
        if (game != null) {
            event.setSpawnLocation(game.getMap().getManifest().getWorld().getSpawn());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.createSession(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        DestroyedPlayerInfo info = this.destroySession(event.getPlayer());
        if (info != null && info.player != null) {
            // make GamePlayers offline
            GamePlayer game = info.player.getGamePlayer();
            if (game != null) {
                game.setPlayer(null); // remove the pointer
                game.setParticipating(false);
                game.onDisconnect(info.player);
            }
        }
    }

    @Handler(priority = Priority.FIRST)
    public void onPluginReady(PluginReadyEvent event) {
        int i = 0;
        for (Player online : event.getServer().getOnlinePlayers()) {
            this.createSession(online);
            i++;
        }

        if (i > 0) {
            event.getPlugin().getLogger().info("Registered " + i + " online player(s).");
        }
    }

    //
    // Death Event
    //

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        ArcadePlayer victim = this.getPlugin().getPlayer(event.getEntity());

        Player victimBukkit = victim.getBukkit();
        if (victimBukkit == null) {
            return;
        }

        ArcadePlayer killer = this.getPlugin().getPlayer(victimBukkit.getKiller());

        Game game = this.getPlugin().getGames().getCurrentGame();
        if (game != null) {
            pl.themolka.arcade.life.PlayerDeathEvent deathEvent = new pl.themolka.arcade.life.PlayerDeathEvent(
                    this.getPlugin(),
                    victim,
                    killer != null ? killer.getGamePlayer() : null,
                    event.getDeathMessage(),
                    event.getDroppedExp(),
                    new ArrayList<>(event.getDrops()));
            deathEvent.setKeepInventory(event.getKeepInventory());
            deathEvent.setKeepLevel(event.getKeepLevel());
            deathEvent.setNewExp(event.getNewExp());
            deathEvent.setNewLevel(event.getNewLevel());

            this.publish(deathEvent);

            this.replaceDrops(event.getDrops(), deathEvent.getDropItems());

            event.setDeathMessage(deathEvent.getDeathMessage());
            event.setDroppedExp(deathEvent.getDropExp());
            event.setKeepInventory(deathEvent.shouldKeepInventory());
            event.setKeepLevel(deathEvent.shouldKeepLevel());
            event.setNewExp(deathEvent.getNewExp());
            event.setNewLevel(deathEvent.getNewLevel());

            if (deathEvent.willAutoRespawn()) {
                this.getPlugin().getServer().getScheduler().runTaskLater(this.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        victim.respawn();
                    }
                }, deathEvent.getAutoRespawnCooldown().toTicks());
            }
        }
    }

    //
    // Respawn Event
    //

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        ArcadePlayer player = this.getPlugin().getPlayer(event.getPlayer().getUniqueId());

        Game game = this.getPlugin().getGames().getCurrentGame();
        if (game != null) {
            pl.themolka.arcade.respawn.PlayerRespawnEvent respawnEvent = new pl.themolka.arcade.respawn.PlayerRespawnEvent(
                    this.getPlugin(),
                    player);
            respawnEvent.setRespawnPosition(game.getMap().getManifest().getWorld().getSpawn());

            this.publish(respawnEvent);

            Location respawnPosition = respawnEvent.getRespawnPosition();
            if (respawnPosition != null) {
                event.setRespawnLocation(respawnPosition);
            }
        }
    }

    //
    // Create Session
    //

    public CreatedPlayerInfo createSession(Player bukkit) {
        ArcadePlayer player = new ArcadePlayer(this.getPlugin(), bukkit);
        boolean restored = false;

        Game game = this.getPlugin().getGames().getCurrentGame();
        if (game != null) {
            // try to restore the GamePlayer first
            GamePlayer gamePlayer = game.resolve(bukkit);
            if (gamePlayer != null) {
                restored = true;
            } else {
                gamePlayer = new GamePlayer(game, player);
            }

            // link objects
            gamePlayer.setPlayer(player);
            player.setGamePlayer(gamePlayer);

            // register
            game.getPlayers().playerJoin(gamePlayer);

            gamePlayer.setParticipating(false); // I don't know ;d
            gamePlayer.reset();
            gamePlayer.refreshVisibilityArcadePlayer(this.getPlugin().getPlayers());
        }

        this.getPlugin().addPlayer(player);
        this.publish(new pl.themolka.arcade.session.PlayerJoinEvent(this.getPlugin(), player, restored));

        return new CreatedPlayerInfo(player, restored);
    }

    private class CreatedPlayerInfo {
        ArcadePlayer player;
        boolean restored;

        CreatedPlayerInfo(ArcadePlayer player, boolean restored) {
            this.player = player;
            this.restored = restored;
        }
    }

    //
    // Destroy Session
    //

    public DestroyedPlayerInfo destroySession(Player bukkit) {
        ArcadePlayer player = this.getPlugin().getPlayer(bukkit.getUniqueId());
        if (player == null) {
            return null;
        }

        this.publish(new pl.themolka.arcade.session.PlayerQuitEvent(this.getPlugin(), player));
        this.getPlugin().removePlayer(player);

        return new DestroyedPlayerInfo(player);
    }

    private class DestroyedPlayerInfo {
        ArcadePlayer player;

        DestroyedPlayerInfo(ArcadePlayer player) {
            this.player = player;
        }
    }
}
