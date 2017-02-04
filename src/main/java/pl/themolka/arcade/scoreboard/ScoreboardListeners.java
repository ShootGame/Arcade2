package pl.themolka.arcade.scoreboard;

import net.engio.mbassy.listener.Handler;
import org.bukkit.event.Listener;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.PluginReadyEvent;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.ServerCycleEvent;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.session.PlayerJoinEvent;

public class ScoreboardListeners implements Listener {
    private final ArcadePlugin plugin;

    public ScoreboardListeners(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    // player joins the server
    @Handler(priority = Priority.NORMAL)
    public void onPlayerJoinServer(PlayerJoinEvent event) {
        this.render(event.getPlayer());
    }

    // server cycles the game
    @Handler(priority = Priority.NORMAL)
    public void onServerCycle(ServerCycleEvent event) {
        for (ArcadePlayer player : event.getPlugin().getPlayers()) {
            this.render(player);
        }
    }

    // server starts
    @Handler(priority = Priority.NORMAL)
    public void onServerStart(PluginReadyEvent event) {
        for (ArcadePlayer player : event.getPlugin().getPlayers()) {
            this.render(player);
        }
    }

    @Handler(priority = Priority.FIRST)
    public void onScoreboardUnregister(ServerCycleEvent event) {
        if (event.getOldGame() == null) {
            return;
        }

        Game game = this.plugin.getGames().getCurrentGame();
        if (game == null) {
            return;
        }

        game.getScoreboard().unregister();
    }

    public void render(ArcadePlayer player) {
        if (player != null) {
            Game game = this.plugin.getGames().getCurrentGame();
            if (game == null) {
                return;
            }

            player.getBukkit().setScoreboard(game.getScoreboard().getScoreboard());
        }
    }
}
