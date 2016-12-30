package pl.themolka.arcade.match;

import org.bukkit.ChatColor;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.task.PrintableCountdown;

import java.time.Duration;

public class MatchStartCountdown extends PrintableCountdown {
    private final ArcadePlugin plugin;

    private final Match match;

    public MatchStartCountdown(ArcadePlugin plugin, Duration duration, Match match) {
        super(plugin.getTasks(), duration);
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
        if (!this.getMatch().isForceStart() && this.cannotStart()) {
            this.cancelCountdown();
            return;
        }

        if (this.isPrintable(secondsLeft)) {
            this.plugin.getServer().broadcastMessage(this.getPrintMessage(this.getStartMessage()));
        }
    }

    private boolean cannotStart() {
        return false; // TODO
    }

    private String getCancelMessage() {
        String message = ChatColor.GREEN + "Start countdown has been canceled";

        if (this.cannotStart()) {
            return message + " due it cannot start.";
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
