package pl.themolka.arcade.game;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.map.OfflineMap;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.task.PrintableCountdown;

import java.time.Duration;

public class CycleCountdown extends PrintableCountdown {
    public static final Duration DEFAULT_DURATION = Duration.ofSeconds(15);
    public static final String FIELD_MAP_NAME = "%MAP_NAME%";

    private final ArcadePlugin plugin;

    public CycleCountdown(ArcadePlugin plugin, Duration duration) {
        super(plugin.getTasks(), duration);

        this.plugin = plugin;

        this.setBossBar(plugin.getServer().createBossBar(new TextComponent(), BarColor.BLUE, BarStyle.SOLID));
    }

    @Override
    public void onCancel() {
        OfflineMap nextMap = this.plugin.getGames().getQueue().getNextMap();
        if (nextMap == null) {
            return;
        }

        this.getBossBar().setVisible(false);
    }

    @Override
    public void onDone() {
        this.plugin.getGames().cycle(null);
        this.getBossBar().setVisible(false);
    }

    @Override
    public void onTick(long ticks) {
        if (!this.plugin.getGames().getQueue().hasNextMap()) {
            this.cancelCountdown();
            return;
        } else if (this.getProgress() > 1) {
            return;
        }

        OfflineMap nextMap = this.plugin.getGames().getQueue().getNextMap();
        Game game = this.plugin.getGames().getCurrentGame();
        if (game == null) {
            return;
        }

        String message = this.getPrintMessage(this.getCycleMessage(nextMap.getName()));
        this.getBossBar().setProgress(this.getProgress());
        this.getBossBar().setTitle(new TextComponent(message));

        for (ArcadePlayer player : this.plugin.getPlayers()) {
            this.getBossBar().addPlayer(player.getBukkit());
        }

        this.getBossBar().setVisible(true);
    }

    @Override
    public void onUpdate(long seconds, long secondsLeft) {
        if (!this.plugin.getGames().getQueue().hasNextMap()) {
            this.cancelCountdown();
            return;
        } else if (!this.isPrintable(secondsLeft)) {
            return;
        }

        OfflineMap nextMap = this.plugin.getGames().getQueue().getNextMap();
        Game game = this.plugin.getGames().getCurrentGame();
        if (game == null) {
            return;
        }

        String message = this.getPrintMessage(this.getCycleMessage(nextMap.getName()));

        for (ArcadePlayer player : this.plugin.getPlayers()) {
            player.getPlayer().send(message);
        }

        this.plugin.getLogger().info(message);
    }

    public void setDefaultDuration() {
        this.setDuration(DEFAULT_DURATION);
    }

    private String getCancelMessage(String mapName) {
        String message = ChatColor.GRAY + "Cycle countdown to " + ChatColor.AQUA + ChatColor.BOLD + FIELD_MAP_NAME +
                ChatColor.RESET + ChatColor.GRAY + " has been canceled.";

        return message.replace(FIELD_MAP_NAME, mapName);
    }

    private String getCycleMessage(String mapName) {
        String message = ChatColor.GRAY + "Cycling to " + ChatColor.AQUA + ChatColor.BOLD + FIELD_MAP_NAME +
                ChatColor.GRAY + " in " + ChatColor.AQUA + ChatColor.BOLD + FIELD_TIME_LEFT + ChatColor.GRAY + ".";

        String result = message.replace(FIELD_MAP_NAME, mapName);

        if (this.isDone()) {
            return result + "..";
        }
        return result;
    }
}
