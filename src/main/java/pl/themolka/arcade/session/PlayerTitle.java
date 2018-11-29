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

package pl.themolka.arcade.session;

import net.md_5.bungee.api.chat.BaseComponent;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.PlayerApplicable;
import pl.themolka.arcade.time.Time;
import pl.themolka.arcade.time.TimeUtils;

import java.util.Objects;

public class PlayerTitle implements PlayerApplicable {
    public static final Time DEFAULT_FADE_IN = Time.ofTicks(10);
    public static final Time DEFAULT_VIEW_TIME = Time.ofSeconds(6);
    public static final Time DEFAULT_FADE_OUT = Time.ofSeconds(2);

    private final BaseComponent primary;
    private final BaseComponent secondary;

    private Time fadeIn = DEFAULT_FADE_IN;
    private Time viewTime = DEFAULT_VIEW_TIME;
    private Time fadeOut = DEFAULT_FADE_OUT;

    public PlayerTitle(BaseComponent primary, BaseComponent secondary) {
        this.primary = Objects.requireNonNull(primary, "primary cannot be null");
        this.secondary = Objects.requireNonNull(secondary, "secondary cannot be null");
    }

    @Override
    public void apply(GamePlayer player) {
        player.getBukkit().showTitle(this.primary, this.secondary,
                TimeUtils.toTicksInt(this.fadeIn),
                TimeUtils.toTicksInt(this.viewTime),
                TimeUtils.toTicksInt(this.fadeOut));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PlayerTitle) {
            PlayerTitle that = (PlayerTitle) obj;
            return  this.primary == that.primary &&
                    this.secondary == that.secondary;
        }

        return false;
    }

    public BaseComponent getPrimary() {
        return this.primary;
    }

    public BaseComponent getSecondary() {
        return this.secondary;
    }

    public Time getFadeIn() {
        return this.fadeIn;
    }

    public Time getViewTime() {
        return this.viewTime;
    }

    public Time getFadeOut() {
        return this.fadeOut;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.primary, this.secondary);
    }

    public void setFadeIn(Time fadeIn) {
        this.fadeIn = Objects.requireNonNull(fadeIn, "fadeIn cannot be null");
    }

    public void setViewTime(Time viewTime) {
        this.viewTime = Objects.requireNonNull(viewTime, "viewTime cannot be null");
    }

    public void setFadeOut(Time fadeOut) {
        this.fadeOut = Objects.requireNonNull(fadeOut, "fadeOut cannot be null");
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("primary", this.primary)
                .append("secondary", this.secondary)
                .append("fadeIn", this.fadeIn)
                .append("viewTime", this.viewTime)
                .append("fadeOut", this.fadeOut)
                .build();
    }
}
