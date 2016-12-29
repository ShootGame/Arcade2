package pl.themolka.arcade.session;

import com.google.common.eventbus.Subscribe;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.PluginReadyEvent;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;

public class Sessions extends pl.themolka.commons.session.Sessions<ArcadeSession> implements Listener {
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

    @Subscribe
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

    public ArcadeSession createSession(Player bukkit) {
        ArcadePlayer player = new ArcadePlayer(bukkit);
        this.plugin.addPlayer(player);

        Game game = this.plugin.getGames().getCurrentGame();

        if (game != null) {
            GamePlayer gamePlayer = game.getPlayer(bukkit.getUniqueId());
            if (gamePlayer == null) {
                gamePlayer = new GamePlayer(game, player);
            }

            player.setGamePlayer(gamePlayer);
            game.addPlayer(gamePlayer);
        }

        this.plugin.getEvents().post(new ArcadePlayerJoinEvent(this.plugin, player));
        return new ArcadeSession(player);
    }

    public ArcadeSession destroySession(Player bukkit) {
        ArcadeSession session = (ArcadeSession) this.getSession(bukkit.getUniqueId());
        session.getRepresenter().getGamePlayer().setPlayer(null); // make offline

        this.plugin.removePlayer(session.getRepresenter());
        this.plugin.getEvents().post(new ArcadePlayerQuitEvent(this.plugin, session.getRepresenter()));

        return session;
    }
}
