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

package pl.themolka.arcade.filter.operator;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.IGameConfig;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class Operator implements Filter {
    private final Set<Filter> body = new LinkedHashSet<>();

    protected Operator(Game game, IGameConfig.Library library, Config<?> config) {
        for (Filter.Config<?> filter : config.body().get()) {
            this.body.add(library.getOrDefine(game, filter));
        }
    }

    public Set<Filter> getBody() {
        return new LinkedHashSet<>(this.body);
    }

    public interface Config<T extends Operator> extends Filter.Config<T> {
        Ref<Set<Filter.Config<?>>> body();
    }
}
