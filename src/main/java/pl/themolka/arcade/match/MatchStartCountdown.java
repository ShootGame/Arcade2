package pl.themolka.arcade.match;

import org.bukkit.ChatColor;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.task.PrintableCountdown;

import java.time.Duration;

public class MatchStartCountdown extends PrintableCountdown {
    private final ArcadePlugin plugin;

    private final Match match;

    public MatchStartCountdown(ArcadePlugin plugin, Match match) {
        super(plugin.getTasks(), null);
        this.plugin = plugin;

        this.match = match;
    }

    @Override
    public void onCancel() {
        this.plugin.getServer().broadcastMessage(this.getCancelMessage());
    }

    @Override
    public void onDone() {
        this.getMatch().start(false);
    }

    @Override
    public void onUpdate(long seconds, long secondsLeft) {
        if (!this.getMatch().isForceStart() && this.getMatch().cannotStart()) {
            this.cancelCountdown();
        } else if (this.isPrintable(secondsLeft)) {
            this.plugin.getServer().broadcastMessage(this.getPrintMessage(this.getStartMessage()));
        }
    }

    public int countStart(int seconds) {
        this.setDuration(Duration.ofSeconds(seconds));

        if (this.isTaskRunning()) {
            return this.getTaskId();
        }
        return this.countSync();
    }

    private String getCancelMessage() {
        String message = ChatColor.GREEN + "Start countdown has been canceled";

        if (!this.getMatch().isForceStart() && this.getMatch().cannotStart()) {
            return message + " due the match cannot start.";
        }
        return message + ".";
    }

    public Match getMatch() {
        return this.match;
    }

    private String getStartMessage() {
        String message = ChatColor.GREEN + "Match starting in " + ChatColor.DARK_RED + FIELD_TIME_LEFT + ChatColor.GREEN + ".";

        if (this.isDone()) {
            return message + "..";
        }
        return message;
    }
}
