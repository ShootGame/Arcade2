package pl.themolka.arcade.bossbar;

import net.engio.mbassy.listener.Handler;
import org.bukkit.event.Listener;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.GameStopEvent;
import pl.themolka.arcade.session.ArcadePlayer;

public class BossBarListeners implements Listener {
    private final ArcadePlugin plugin;

    public BossBarListeners(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    @Handler(priority = Priority.HIGHEST)
    public void removeAll(GameStopEvent event) {
        for (ArcadePlayer online : this.plugin.getPlayers()) {
            GamePlayer player = online.getGamePlayer();
            if (player != null) {
                player.getBossBarContext().removeAll();
            }
        }
    }
}
