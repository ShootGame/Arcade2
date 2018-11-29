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
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.builder.Builder;
import pl.themolka.arcade.time.Time;

public class PlayerTitleBuilder implements Builder<PlayerTitle> {
    private BaseComponent primary = new TextComponent();
    private BaseComponent secondary = new TextComponent();

    private Time fadeIn;
    private Time viewTime;
    private Time fadeOut;

    @Override
    public PlayerTitle build() {
        PlayerTitle title = new PlayerTitle(this.primary(), this.secondary());

        Time fadeIn = this.fadeIn();
        if (fadeIn != null) {
            title.setFadeIn(fadeIn);
        }

        Time viewTime = this.viewTime();
        if (viewTime != null) {
            title.setViewTime(viewTime);
        }

        Time fadeOut = this.fadeOut();
        if (fadeOut != null) {
            title.setFadeOut(fadeOut);
        }

        return title;
    }

    public BaseComponent primary() {
        return this.primary;
    }

    public PlayerTitleBuilder primary(BaseComponent primary) {
        this.primary = primary != null ? primary : new TextComponent();
        return this;
    }

    public BaseComponent secondary() {
        return this.secondary;
    }

    public PlayerTitleBuilder secondary(BaseComponent secondary) {
        this.secondary = secondary != null ? secondary : new TextComponent();
        return this;
    }

    public Time fadeIn() {
        return this.fadeIn;
    }

    public PlayerTitleBuilder fadeIn(Time fadeIn) {
        this.fadeIn = fadeIn;
        return this;
    }

    public Time viewTime() {
        return this.viewTime;
    }

    public PlayerTitleBuilder viewTime(Time viewTime) {
        this.viewTime = viewTime;
        return this;
    }

    public Time fadeOut() {
        return this.fadeOut;
    }

    public PlayerTitleBuilder fadeOut(Time fadeOut) {
        this.fadeOut = fadeOut;
        return this;
    }
}
