package pl.themolka.arcade.filter;

import pl.themolka.arcade.condition.AbstainableResult;
import pl.themolka.arcade.condition.AnyCondition;
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

    protected FilterSet(Game game, Config config) {
        this.id = config.id();

        for (Filter.Config<?> filter : config.filters().get()) {
            this.filters.add(filter.create(game));
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
        return new AnyCondition(this.filters).query(objects);
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
        default FilterSet create(Game game) {
            return new FilterSet(game, this);
        }
    }
}
