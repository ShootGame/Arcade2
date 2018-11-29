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

package pl.themolka.arcade.objective.flag;

import pl.themolka.arcade.game.Participator;

public interface IFlag {
    void tick(Tick tick);

    class Tick {
        private final long ticks;
        private final Participator owner;

        public Tick(long ticks,
                    Participator owner) {
            this.ticks = ticks;
            this.owner = owner;
        }

        public long getTicks() {
            return this.ticks;
        }

        public Participator getOwner() {
            return this.owner;
        }
    }
}
