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
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Event;
import pl.themolka.arcade.event.PluginReadyEvent;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;

public class Sessions implements Listener {
    private final ArcadePlugin plugin;

    public Sessions(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInitialSpawn(PlayerInitialSpawnEvent event) {
        event.setSpawnLocation(this.fetchSpawn());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.insertSession(this.createSession(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        ArcadePlayer player = this.destroySession(event.getPlayer());
        if (player != null) {
            this.removeSession(player);
        }
    }

    @Handler(priority = Priority.FIRST)
    public void onPluginReady(PluginReadyEvent event) {
        int i = 0;
        for (Player online : event.getServer().getOnlinePlayers()) {
            this.insertSession(this.createSession(online));
            i++;
        }

        if (i > 0) {
            event.getPlugin().getLogger().info(
                    "Registered " + i + " online player(s).");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        ArcadePlayer victim = this.plugin.getPlayer(event.getEntity());
        ArcadePlayer killer = this.plugin.getPlayer(victim.getBukkit().getKiller());

        Game game = this.plugin.getGames().getCurrentGame();
        if (game != null) {
            pl.themolka.arcade.life.PlayerDeathEvent deathEvent =
                    new pl.themolka.arcade.life.PlayerDeathEvent(
                            this.plugin,
                            victim,
                            killer != null ? killer.getGamePlayer() : null,
                            event.getDeathMessage(),
                            event.getDroppedExp(),
                            event.getDrops());
            deathEvent.setKeepInventory(event.getKeepInventory());
            deathEvent.setKeepLevel(event.getKeepLevel());
            deathEvent.setNewExp(event.getNewExp());
            deathEvent.setNewLevel(event.getNewLevel());

            this.postEvent(deathEvent);

            event.getDrops().clear();
            event.getDrops().addAll(deathEvent.getDroppedItems());

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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        ArcadePlayer player = this.plugin.getPlayer(
                event.getPlayer().getUniqueId());

        Game game = this.plugin.getGames().getCurrentGame();
        if (game != null) {
            pl.themolka.arcade.respawn.PlayerRespawnEvent respawnEvent =
                    new pl.themolka.arcade.respawn.PlayerRespawnEvent(
                            this.plugin, player);
            respawnEvent.setRespawnPosition(game.getMap().getSpawn());

            this.postEvent(respawnEvent);
            if (respawnEvent.getRespawnPosition() != null) {
                event.setRespawnLocation(respawnEvent.getRespawnPosition());
            }
        }
    }

    public ArcadePlayer createSession(Player bukkit) {
        ArcadePlayer player = new ArcadePlayer(this.plugin, bukkit);

        Game game = this.plugin.getGames().getCurrentGame();
        if (game != null) {
            // try to restore the GamePlayer first
            GamePlayer gamePlayer = game.getPlayer(bukkit);
            if (gamePlayer == null) {
                gamePlayer = new GamePlayer(game, player);
            }

            gamePlayer.setPlayer(player);
            player.setGamePlayer(gamePlayer);
            game.addPlayer(gamePlayer);

            gamePlayer.setParticipating(false); // I don't know ;d
            gamePlayer.reset();
        }

        this.plugin.addPlayer(player);
        return player;
    }

    public ArcadePlayer destroySession(Player bukkit) {
        ArcadePlayer player = this.plugin.getPlayer(bukkit.getUniqueId());
        if (player == null) {
            return null;
        }

        // unregister from online players
        this.plugin.removePlayer(player);

        // make GamePlayers offline
        GamePlayer game = player.getGamePlayer();
        if (game != null) {
            game.setPlayer(null); // remove the pointer
        }

        return player;
    }

    public void insertSession(ArcadePlayer player) {
        this.postEvent(new pl.themolka.arcade.session
                .PlayerJoinEvent(this.plugin, player));
    }

    public void removeSession(ArcadePlayer player) {
        this.postEvent(new pl.themolka.arcade.session
                .PlayerQuitEvent(this.plugin, player));
    }

    private Location fetchSpawn() {
        Game game = this.plugin.getGames().getCurrentGame();
        if (game != null) {
            return game.getMap().getSpawn();
        }

        return null;
    }

    private void postEvent(Event event) {
        this.plugin.getEventBus().publish(event);
    }
}
