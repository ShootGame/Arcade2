/*
 * Copyright 2018 Aleksander Jagiełło
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.themolka.arcade.match;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.bossbar.BarPriority;
import pl.themolka.arcade.bossbar.BossBar;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.session.ArcadeSound;
import pl.themolka.arcade.task.PrintableCountdown;
import pl.themolka.arcade.util.Percentage;

import java.time.Duration;

public class MatchStartCountdown extends PrintableCountdown {
    public static final int BAR_PRIORITY = BarPriority.HIGHEST;

    private final ArcadePlugin plugin;

    private boolean cannotStart;
    private final Match match;

    public MatchStartCountdown(ArcadePlugin plugin, Match match) {
        super(plugin.getTasks(), null);
        this.plugin = plugin;

        this.match = match;
        this.setBossBar(new BossBar(BarColor.GREEN, BarStyle.SOLID));
    }

    @Override
    public void onCancel() {
        this.removeBossBars(this.plugin.getPlayers());
    }

    @Override
    public void onDone() {
        this.removeBossBars(this.plugin.getPlayers());
        this.getMatch().start(false);
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

        BossBar bossBar = this.getBossBar();
        bossBar.setProgress(Percentage.finite(this.getProgress()));
        bossBar.setText(new TextComponent(message));

        for (ArcadePlayer online : this.plugin.getPlayers()) {
            GamePlayer player = online.getGamePlayer();
            if (player != null) {
                bossBar.addPlayer(player, BAR_PRIORITY);
            }
        }
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
            text = "";
        } else if (left == 1 || left == 2 || left == 3) {
            text = ChatColor.YELLOW + Long.toString(left);
        }

        if (text != null) {
            String start = ChatColor.GREEN + ChatColor.ITALIC.toString() + "The match has started.";

            Observers observers = this.match.getObservers();
            for (ArcadePlayer online : this.plugin.getPlayers()) {
                GamePlayer player = online.getGamePlayer();
                if (player == null) {
                    continue;
                }

                if (observers.contains(online)) {
                    if (left == 0) {
                        player.getBukkit().sendTitle(text, start, 3, 60, 10);
                    }
                } else {
                    player.getBukkit().sendTitle(text, "", 3, 5, 20);
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
