package pl.themolka.arcade.game;

import org.bukkit.ChatColor;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.task.PrintableCountdown;

import java.time.Duration;

public class RestartCountdown extends PrintableCountdown {
    public static final String FIELD_SERVER_NAME = "%SERVER_NAME%";

    private final ArcadePlugin plugin;

    public RestartCountdown(ArcadePlugin plugin, Duration duration) {
        super(plugin.getTasks(), duration);

        this.plugin = plugin;
    }

    @Override
    public void onCancel() {
        this.plugin.getServer().broadcastMessage(this.getCancelMessage());
    }

    @Override
    public void onDone() {
        this.plugin.getGames().cycleRestart();
    }

    @Override
    public void onUpdate(long seconds, long secondsLeft) {
        if (!this.plugin.getGames().isNextRestart()) {
            this.cancelCountdown();
        } else if (this.isPrintable(secondsLeft)) {
            this.plugin.getServer().broadcastMessage(this.getPrintMessage(this.getRestartMessage()));
        }
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
