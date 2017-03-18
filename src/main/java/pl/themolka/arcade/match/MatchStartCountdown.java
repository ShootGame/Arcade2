package pl.themolka.arcade.match;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.session.ArcadeSound;
import pl.themolka.arcade.task.PrintableCountdown;

import java.time.Duration;

public class MatchStartCountdown extends PrintableCountdown {
    private final ArcadePlugin plugin;

    private boolean cannotStart;
    private final Match match;

    public MatchStartCountdown(ArcadePlugin plugin, Match match) {
        super(plugin.getTasks(), null);
        this.plugin = plugin;

        this.match = match;
        this.setBossBar(plugin.getServer().createBossBar(new TextComponent(), BarColor.GREEN, BarStyle.SOLID));
    }

    @Override
    public void onCancel() {
        this.getBossBar().setVisible(false);
    }

    @Override
    public void onDone() {
        this.getMatch().start(false);
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

        String message = this.getPrintMessage(this.getStartMessage());
        this.getBossBar().setProgress(this.getProgress());
        this.getBossBar().setTitle(new TextComponent(message));

        for (ArcadePlayer player : this.plugin.getPlayers()) {
            this.getBossBar().addPlayer(player.getBukkit());
        }

        this.getBossBar().setVisible(true);
    }

    @Override
    public void onUpdate(long seconds, long secondsLeft) {
        MatchStartCountdownEvent event = new MatchStartCountdownEvent(this.plugin, this.match, this);
        this.plugin.getEventBus().publish(event);

        if (!this.getMatch().isForceStart() && (event.isCanceled() || this.cannotStart)) {
            this.cannotStart = false;
            this.cancelCountdown();
            return;
        } else if (!this.isPrintable(secondsLeft)) {
            return;
        }

        Game game = this.plugin.getGames().getCurrentGame();
        if (game == null) {
            return;
        }

        this.printMessage();
        this.printCount();
        this.playSound();
    }

    public int countStart(int seconds) {
        this.setDuration(Duration.ofSeconds(seconds));

        if (this.isTaskRunning()) {
            this.cancelCountdown();
        }

        MatchStartCountdownEvent event = new MatchStartCountdownEvent(this.plugin, this.match, this);
        this.plugin.getEventBus().publish(event);

        if (!event.isCanceled()) {
            this.cancelCountdown();
            return this.countSync();
        }

        return -1;
    }

    private String getCancelMessage() {
        String message = ChatColor.GREEN + "Start countdown has been canceled";

        if (!this.getMatch().isForceStart() && this.cannotStart) {
            return message + " due the match cannot start.";
        }
        return message + ".";
    }

    public Match getMatch() {
        return this.match;
    }

    private String getStartMessage() {
        String message = ChatColor.GREEN + "Match starting in " + ChatColor.GOLD + ChatColor.BOLD +
                FIELD_TIME_LEFT + ChatColor.RESET + ChatColor.GREEN + ".";

        if (this.isDone()) {
            return message + "..";
        }
        return message;
    }

    private void playSound() {
        long left = this.getLeftSeconds();

        ArcadeSound sound = null;
        if (left == 0) {
            sound = ArcadeSound.STARTED;
        } else if (left == 1 || left == 2 || left == 3) {
            sound = ArcadeSound.STARTING;
        }

        if (sound != null) {
            for (ArcadePlayer player : this.plugin.getPlayers()) {
                player.play(sound);
            }
        }
    }

    private void printCount() {
        long left = this.getLeftSeconds();

        String text = null;
        if (left == 0) {
            text = ChatColor.RED + ChatColor.UNDERLINE.toString() + "GO!";
        } else if (left == 1 || left == 2 || left == 3) {
            text = ChatColor.YELLOW + Long.toString(left);
        }

        if (text != null) {
            BaseComponent[] component = TextComponent.fromLegacyText(text);
            BaseComponent[] infoComponent = TextComponent.fromLegacyText(
                    ChatColor.YELLOW + ChatColor.UNDERLINE.toString() + "The match has started.");

            for (ArcadePlayer online : this.plugin.getPlayers()) {
                GamePlayer player = online.getGamePlayer();
                if (player == null) {
                    continue;
                }

                if (!this.getMatch().getObservers().contains(online)) {
                    player.getBukkit().showTitle(component);
                } else if (left == 0) {
                    player.getBukkit().showTitle(infoComponent);
                }
            }
        }
    }

    private void printMessage() {
        String message = this.getPrintMessage(this.getStartMessage());
        for (ArcadePlayer player : this.plugin.getPlayers()) {
            player.getPlayer().send(message);
        }

        this.plugin.getLogger().info(ChatColor.stripColor(message));
    }
}
