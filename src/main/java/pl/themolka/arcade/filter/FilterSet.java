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

package pl.themolka.arcade.filter;

import pl.themolka.arcade.condition.AbstainableResult;
import pl.themolka.arcade.condition.OrCondition;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.config.Unique;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.IGameConfig;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a set of instructions which must be filtered.
 */
public class FilterSet implements UniqueFilter {
    private final Set<Filter> filters = new LinkedHashSet<>();
    private final String id;

    protected FilterSet(Game game, IGameConfig.Library library, Config config) {
        this.id = config.id();

        for (Filter.Config<?> filter : config.filters().get()) {
            this.filters.add(library.getOrDefine(game, filter));
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof UniqueFilter && ((FilterSet) obj).id.equals(this.id);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public AbstainableResult filter(Object... objects) {
        return new OrCondition(this.filters).query(objects);
    }

    public boolean addFilter(Filter filter) {
        return this.filters.add(Objects.requireNonNull(filter, "filter cannot be null"));
    }

    public Set<Filter> getFilters() {
        return new HashSet<>(this.filters);
    }

    public boolean isEmpty() {
        return this.filters.isEmpty();
    }

    public boolean removeFilter(Filter filter) {
        return this.filters.remove(filter);
    }

    public interface Config extends IGameConfig<FilterSet>, Unique {
        Ref<Set<Filter.Config<?>>> filters();

        @Override
        default FilterSet create(Game game, Library library) {
            return new FilterSet(game, library, this);
        }
    }
}
