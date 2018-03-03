package pl.themolka.arcade.game;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.bossbar.BarPriority;
import pl.themolka.arcade.bossbar.BossBar;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.task.PrintableCountdown;
import pl.themolka.arcade.util.Percentage;

import java.time.Duration;

public class RestartCountdown extends PrintableCountdown {
    public static final Duration DEFAULT_DURATION = Duration.ofSeconds(15);
    public static final String FIELD_SERVER_NAME = "%SERVER_NAME%";
    public static final int BAR_PRIORITY = BarPriority.FIRST;

    private final ArcadePlugin plugin;

    public RestartCountdown(ArcadePlugin plugin, Duration duration) {
        super(plugin.getTasks(), duration);

        this.plugin = plugin;

        this.setBossBar(new BossBar(BarColor.RED, BarStyle.SOLID));
    }

    @Override
    public void onCancel() {
        this.removeBossBars(this.plugin.getPlayers());
    }

    @Override
    public void onDone() {
        this.removeBossBars(this.plugin.getPlayers());
        this.plugin.getGames().cycleRestart();
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
        this.getBossBar().setProgress(Percentage.finite(this.getProgress()));
        this.getBossBar().setText(new TextComponent(message));

        for (ArcadePlayer online : this.plugin.getPlayers()) {
            GamePlayer player = online.getGamePlayer();
            if (player != null) {
                player.getBossBarContext().addBossBar(this.getBossBar(), BAR_PRIORITY);
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
        for (ArcadePlayer player : this.plugin.getPlayers()) {
            player.getPlayer().send(message);
        }

        this.plugin.getLogger().info(ChatColor.stripColor(message));
    }

    public void setDefaultDuration() {
        this.setDuration(DEFAULT_DURATION);
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
