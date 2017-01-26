package pl.themolka.arcade.session;

import net.engio.mbassy.listener.Handler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Event;
import pl.themolka.arcade.event.PluginReadyEvent;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.respawn.ArcadePlayerRespawnEvent;

public class Sessions implements Listener {
    private final ArcadePlugin plugin;

    public Sessions(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.insertSession(this.createSession(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.removeSession(this.destroySession(event.getPlayer()));
    }

    @Handler(priority = Priority.FIRST)
    public void onPluginReady(PluginReadyEvent event) {
        int i = 0;
        for (Player online : event.getServer().getOnlinePlayers()) {
            this.insertSession(this.createSession(online));
            i++;
        }

        if (i > 0) {
            event.getPlugin().getLogger().info("Registered " + i + " online player(s).");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        ArcadePlayer player = this.plugin.getPlayer(event.getPlayer().getUniqueId());

        Game game = this.plugin.getGames().getCurrentGame();
        if (game != null) {
            ArcadePlayerRespawnEvent respawnEvent = new ArcadePlayerRespawnEvent(this.plugin, player);
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
            GamePlayer gamePlayer = game.getPlayer(bukkit.getUniqueId());
            if (gamePlayer == null) {
                gamePlayer = new GamePlayer(game, player);
            }

            gamePlayer.setPlayer(player);
            player.setGamePlayer(gamePlayer);
            game.addPlayer(gamePlayer);

            player.getBukkit().teleport(game.getMap().getSpawn());
            player.resetFull();
        }

        this.plugin.addPlayer(player);
        return player;
    }

    public ArcadePlayer destroySession(Player bukkit) {
        ArcadePlayer player = this.plugin.getPlayer(bukkit.getUniqueId());

        Game game = this.plugin.getGames().getCurrentGame();
        if (game != null) {
            game.removePlayer(player.getGamePlayer());
        }

        if (player.getGamePlayer() != null) {
            player.getGamePlayer().setPlayer(null); // make offline
        }

        this.plugin.removePlayer(player);
        return player;
    }

    public void insertSession(ArcadePlayer player) {
        this.postEvent(new ArcadePlayerJoinEvent(this.plugin, player));
    }

    public void removeSession(ArcadePlayer player) {
        this.postEvent(new ArcadePlayerQuitEvent(this.plugin, player));
    }

    private void postEvent(Event event) {
        this.plugin.getEventBus().publish(event);
    }
}
