package pl.themolka.arcade.game;

import org.bukkit.ChatColor;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.map.OfflineMap;
import pl.themolka.arcade.task.PrintableCountdown;

import java.time.Duration;

public class CycleCountdown extends PrintableCountdown {
    public static final String FIELD_MAP_NAME = "%MAP_NAME%";

    private final ArcadePlugin plugin;

    public CycleCountdown(ArcadePlugin plugin, Duration duration) {
        super(plugin.getTasks(), duration);

        this.plugin = plugin;
    }

    @Override
    public void onCancel() {
        OfflineMap nextMap = this.plugin.getGames().getQueue().getNextMap();
        if (nextMap == null) {
            return;
        }

        this.plugin.getServer().broadcastMessage(this.getCancelMessage(nextMap.getName()));
    }

    @Override
    public void onDone() {
        this.plugin.getGames().cycleNext();
    }

    @Override
    public void onUpdate(long seconds, long secondsLeft) {
        if (!this.plugin.getGames().getQueue().hasNextMap()) {
            this.cancelCountdown();
        } else if (this.isPrintable(secondsLeft)) {
            OfflineMap nextMap = this.plugin.getGames().getQueue().getNextMap();
            if (nextMap == null) {
                return;
            }

            this.plugin.getServer().broadcastMessage(this.getPrintMessage(this.getCycleMessage(nextMap.getName())));
        }
    }

    private String getCancelMessage(String mapName) {
        String message = ChatColor.GRAY + "Cycle countdown to " + ChatColor.GOLD +
                FIELD_MAP_NAME + ChatColor.GRAY + " has been canceled.";

        return message.replace(FIELD_MAP_NAME, mapName);
    }

    private String getCycleMessage(String mapName) {
        String message = ChatColor.GRAY + "Cycling to " + ChatColor.GOLD + ChatColor.BOLD + FIELD_MAP_NAME +
                ChatColor.GRAY + " in " + ChatColor.GOLD + ChatColor.BOLD + FIELD_TIME_LEFT + ChatColor.GRAY + ".";

        String result = message.replace(FIELD_MAP_NAME, mapName);

        if (this.isDone()) {
            return result + "..";
        }
        return result;
    }
}
