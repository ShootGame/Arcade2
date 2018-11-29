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

package pl.themolka.arcade.objective.point;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.util.Color;
import pl.themolka.arcade.util.Progressive;

public interface IPoint extends Progressive {
    Color getColor();

    void tick(Tick tick);

    class Tick {
        private final long tick;
        private final Multimap<Participator, GamePlayer> participators;
        private final Multimap<Participator, GamePlayer> dominators;
        private final Participator owner;

        public Tick(long tick,
                    Multimap<Participator, GamePlayer> participators,
                    Multimap<Participator, GamePlayer> dominators,
                    Participator owner) {
            this.tick = tick;
            this.participators = ImmutableMultimap.copyOf(participators);
            this.dominators = ImmutableMultimap.copyOf(dominators);
            this.owner = owner;
        }

        public long getTick() {
            return this.tick;
        }

        public Multimap<Participator, GamePlayer> getParticipators() {
            return this.participators;
        }

        public Multimap<Participator, GamePlayer> getDominators() {
            return this.dominators;
        }

        public Participator getOwner() {
            return this.owner;
        }
    }
}
