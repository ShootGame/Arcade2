package pl.themolka.arcade.game;

import org.bukkit.ChatColor;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.task.PrintableCountdown;
import pl.themolka.arcade.task.TaskManager;

import java.time.Duration;

public class CycleCountdown extends PrintableCountdown {
    public static final String FIELD_MAP = "%MAP%";

    private final ArcadePlugin plugin;

    public CycleCountdown(TaskManager tasks, Duration duration, ArcadePlugin plugin) {
        super(tasks, duration);

        this.plugin = plugin;
    }

    @Override
    public void onDone() {
        this.plugin.getGames().cycleNext();
    }

    @Override
    public void onUpdate(long seconds, long secondsLeft) {
        if (this.plugin.getGames().getQueue().hasNextMap()) {
            this.cancelTask();
        } else if (this.isPrintable(secondsLeft)) {
            this.plugin.getServer().broadcastMessage(this.getPrintMessage(this.getCycleMessage()));
        }
    }

    private String getCycleMessage() {
        String message = ChatColor.GRAY + "Cycling to " + ChatColor.GOLD + ChatColor.BOLD + FIELD_MAP + ChatColor.GRAY +
                " in " + ChatColor.GOLD + ChatColor.BOLD + FIELD_TIME + ChatColor.GRAY + ".";

        String result = message.replace(FIELD_MAP, this.plugin.getGames().getQueue().getNextMap().getName());

        if (this.isDone()) {
            return result + "..";
        }
        return result;
    }
}
