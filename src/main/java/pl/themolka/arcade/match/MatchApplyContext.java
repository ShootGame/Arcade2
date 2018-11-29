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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.PlayerApplicable;

import java.util.Collection;

public abstract class MatchApplyContext implements PlayerApplicable {
    public static final EventType NONE = EventType.NONE;

    private final Multimap<EventType, PlayerApplicable> container = ArrayListMultimap.create();

    @Override
    @Deprecated
    public void apply(GamePlayer player) {
        this.apply(player, NONE);
    }

    public boolean addContent(PlayerApplicable content) {
        return this.addContent(content, NONE);
    }

    public boolean addContent(PlayerApplicable content, EventType event) {
        return this.container.put(this.formatEvent(event), content);
    }

    public void apply(GamePlayer player, EventType event) {
        if (event == null || event.none()) {
            throw new IllegalArgumentException("Cannot apply none event.");
        }

        this.applyContent(player, event, new Content(this.getContent(event)));
    }

    public void applyAll(GamePlayer player, EventType event) {
        if (event == null || event.none()) {
            throw new IllegalArgumentException("Cannot apply none event.");
        }

        this.applyContent(player, event, new Content(this.getAllContent(event)));
    }

    public void clearContent(EventType event) {
        this.container.removeAll(this.formatEvent(event));
    }

    public Collection<PlayerApplicable> getAllContent(EventType event) {
        Collection<PlayerApplicable> content = this.getContent(event = this.formatEvent(event));

        if (!event.none()) {
            content.addAll(this.getContent(NONE));
        }

        return content;
    }

    public Collection<PlayerApplicable> getContent() {
        return this.getContent(NONE);
    }

    public Collection<PlayerApplicable> getContent(EventType event) {
        return this.container.get(this.formatEvent(event));
    }

    protected abstract void applyContent(GamePlayer player, EventType event, Content content);

    private EventType formatEvent(EventType event) {
        return event != null ? event : NONE;
    }

    protected class Content implements PlayerApplicable {
        private final Collection<PlayerApplicable> content;

        public Content(Collection<PlayerApplicable> content) {
            this.content = content;
        }

        @Override
        public void apply(GamePlayer player) {
            for (PlayerApplicable applicable : this.content) {
                applicable.apply(player);
            }
        }
    }

    public enum EventType {
        NONE,

        MATCH_START,
        PLAYER_PLAY,
        PLAYER_RESPAWN,
        ;

        public boolean none() {
            return this == NONE;
        }
    }
}
