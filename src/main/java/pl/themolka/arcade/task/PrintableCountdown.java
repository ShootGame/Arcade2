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

package pl.themolka.arcade.task;

import pl.themolka.arcade.bossbar.BossBar;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.session.ArcadePlayer;

import java.time.Duration;

public class PrintableCountdown extends Countdown {
    public static final String FIELD_TIME_LEFT = "%TIME_LEFT%";
    public static final int[] SECONDS = {0, 1, 2, 3, 4, 5, 10, 15, 20, 25, 30, 45, 60};

    private BossBar boss;

    public PrintableCountdown(TaskManager tasks) {
        super(tasks);
    }

    public PrintableCountdown(TaskManager tasks, Duration duration) {
        super(tasks, duration);
    }

    public BossBar getBossBar() {
        return this.boss;
    }

    public String getPrintMessage(String message) {
        long left = this.getLeftSeconds();

        String result = Long.toString(left) + " seconds";
        if (left == 1L) {
            result = Long.toString(left) + " second";
        }

        return message.replace(FIELD_TIME_LEFT, result);
    }

    public double getProgress() {
        if (this.getDurationSeconds() > 60) {
            return 0;
        }

        return (1D / this.getDurationSeconds()) * this.getSeconds();
    }

    public boolean isPrintable(long secondsLeft) {
        if (secondsLeft >= 60) {
            return secondsLeft % 60 == 0; // every minute
        }

        for (int second : SECONDS) {
            if (second == secondsLeft) {
                return true;
            }
        }

        return false;
    }

    public void removeBossBars(Iterable<ArcadePlayer> online) {
        BossBar bossBar = this.getBossBar();
        if (bossBar == null) {
            return;
        }

        for (ArcadePlayer onlinePlayer : online) {
            GamePlayer player = onlinePlayer.getGamePlayer();
            if (player != null) {
                bossBar.removePlayer(player);
            }
        }
    }

    public void setBossBar(BossBar boss) {
        this.boss = boss;
    }
}
