package pl.themolka.arcade.session;

import net.engio.mbassy.listener.Handler;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInitialSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Event;
import pl.themolka.arcade.event.PluginReadyEvent;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;

import java.util.ArrayList;
import java.util.List;

public class Sessions implements Listener {
    private final ArcadePlugin plugin;

    public Sessions(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    private void publish(Event event) {
        this.plugin.getEventBus().publish(event);
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
        Game game = this.plugin.getGames().getCurrentGame();
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
        ArcadePlayer victim = this.plugin.getPlayer(event.getEntity());

        Player victimBukkit = victim.getBukkit();
        if (victimBukkit == null) {
            return;
        }

        ArcadePlayer killer = this.plugin.getPlayer(victimBukkit.getKiller());

        Game game = this.plugin.getGames().getCurrentGame();
        if (game != null) {
            pl.themolka.arcade.life.PlayerDeathEvent deathEvent = new pl.themolka.arcade.life.PlayerDeathEvent(
                    this.plugin,
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
                this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable() {
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
        ArcadePlayer player = this.plugin.getPlayer(event.getPlayer().getUniqueId());

        Game game = this.plugin.getGames().getCurrentGame();
        if (game != null) {
            pl.themolka.arcade.respawn.PlayerRespawnEvent respawnEvent = new pl.themolka.arcade.respawn.PlayerRespawnEvent(
                    this.plugin,
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
        ArcadePlayer player = new ArcadePlayer(this.plugin, bukkit);
        boolean restored = false;

        Game game = this.plugin.getGames().getCurrentGame();
        if (game != null) {
            // try to restore the GamePlayer first
            GamePlayer gamePlayer = game.getPlayer(bukkit);
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
            gamePlayer.refreshVisibilityArcadePlayer(this.plugin.getPlayers());
        }

        this.plugin.addPlayer(player);
        this.publish(new pl.themolka.arcade.session.PlayerJoinEvent(this.plugin, player, restored));

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
        ArcadePlayer player = this.plugin.getPlayer(bukkit.getUniqueId());
        if (player == null) {
            return null;
        }

        this.publish(new pl.themolka.arcade.session.PlayerQuitEvent(this.plugin, player));
        this.plugin.removePlayer(player);

        return new DestroyedPlayerInfo(player);
    }

    private class DestroyedPlayerInfo {
        ArcadePlayer player;

        DestroyedPlayerInfo(ArcadePlayer player) {
            this.player = player;
        }
    }
}
