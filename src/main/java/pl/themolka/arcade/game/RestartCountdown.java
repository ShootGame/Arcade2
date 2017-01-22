package pl.themolka.arcade.game;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.task.PrintableCountdown;

import java.time.Duration;

public class RestartCountdown extends PrintableCountdown {
    public static final String FIELD_SERVER_NAME = "%SERVER_NAME%";

    private final ArcadePlugin plugin;

    public RestartCountdown(ArcadePlugin plugin, Duration duration) {
        super(plugin.getTasks(), duration);

        this.plugin = plugin;

        this.setBossBar(plugin.getServer().createBossBar(new TextComponent(), BarColor.RED, BarStyle.SOLID));
    }

    @Override
    public void onCancel() {
        this.getBossBar().setVisible(false);
    }

    @Override
    public void onDone() {
        this.plugin.getGames().cycleRestart();
        this.getBossBar().setVisible(false);
    }

    @Override
    public void onTick(long ticks) {
        Game game = this.plugin.getGames().getCurrentGame();
        if (game == null) {
            return;
        } else if (this.getProgress() > 1) {
            return;
        }

        String message = this.getPrintMessage(this.getRestartMessage());
        this.getBossBar().setProgress(this.getProgress());
        this.getBossBar().setTitle(new TextComponent(message));

        for (GamePlayer player : game.getPlayers()) {
            if (player.isOnline()) {
                this.getBossBar().addPlayer(player.getBukkit());
            }
        }

        this.getBossBar().setVisible(true);
    }

    @Override
    public void onUpdate(long seconds, long secondsLeft) {
        if (!this.plugin.getGames().isNextRestart()) {
            this.cancelCountdown();
            return;
        } else if (!this.isPrintable(secondsLeft)) {
            return;
        }

        Game game = this.plugin.getGames().getCurrentGame();
        if (game == null) {
            return;
        }

        String message = this.getPrintMessage(this.getRestartMessage());
        for (GamePlayer player : game.getPlayers()) {
            if (player.isOnline()) {
                player.getPlayer().send(message);
            }
        }

        this.plugin.getLogger().info(message);
    }

    private String getCancelMessage() {
        return ChatColor.RED + "Restart countdown has been canceled.";
    }

    private String getRestartMessage() {
        String message = ChatColor.RED + "Restarting " + FIELD_SERVER_NAME + " in " + ChatColor.DARK_RED +
                ChatColor.BOLD + FIELD_TIME_LEFT + ChatColor.RED + ".";

        String result = message.replace(FIELD_SERVER_NAME, this.plugin.getServerName());

        if (this.isDone()) {
            result += "..";
        }

        return result;
    }
}
