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

package pl.themolka.arcade.cycle;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.bossbar.BarPriority;
import pl.themolka.arcade.bossbar.BossBar;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.map.OfflineMap;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.task.PrintableCountdown;
import pl.themolka.arcade.util.Percentage;

import java.time.Duration;

public class CycleCountdown extends PrintableCountdown {
    public static final Duration DEFAULT_DURATION = Duration.ofSeconds(15);
    public static final String FIELD_MAP_NAME = "%MAP_NAME%";
    public static final int BAR_PRIORITY = BarPriority.FIRST;

    private final ArcadePlugin plugin;

    public CycleCountdown(ArcadePlugin plugin, Duration duration) {
        super(plugin.getTasks(), duration);

        this.plugin = plugin;

        this.setBossBar(new BossBar(BarColor.BLUE, BarStyle.SOLID));
    }

    @Override
    public void onCancel() {
        this.removeBossBars(this.plugin.getPlayers());
    }

    @Override
    public void onDone() {
        this.removeBossBars(this.plugin.getPlayers());
        this.plugin.getGames().cycle(null);
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

        this.plugin.getLogger().info(ChatColor.stripColor(message));
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
