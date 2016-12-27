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
import pl.themolka.arcade.game.GamePlayer;

public class Sessions extends pl.themolka.commons.session.Sessions<ArcadeSession> implements Listener {
    private final ArcadePlugin plugin;

    public Sessions(ArcadePlugin plugin) {
        this.plugin = plugin;

        plugin.registerListenerObject(this);
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

        GamePlayer game = this.plugin.getGames().getCurrentGame().getPlayer(bukkit.getUniqueId());
        if (game == null) {
            game = new GamePlayer(this.plugin.getGames().getCurrentGame(), player);
        }

        player.setGamePlayer(game);

        this.plugin.getGames().getCurrentGame().addPlayer(game);
        return new ArcadeSession(game.getPlayer());
    }

    public ArcadeSession destroySession(Player bukkit) {
        ArcadeSession session = (ArcadeSession) this.getSession(bukkit.getUniqueId());
        session.getRepresenter().getGamePlayer().setPlayer(null); // make offline

        return session;
    }
}
