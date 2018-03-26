package pl.themolka.arcade.filter;

import pl.themolka.arcade.config.Unique;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.IGameConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FilterSet implements UniqueFilter {
    private final List<Filter> filters = new ArrayList<>();
    private final String id;

    @Deprecated
    public FilterSet(String id) {
        this(id, (Filter[]) null);
    }

    @Deprecated
    public FilterSet(String id, Filter... filters) {
        this.id = id;

        if (filters != null) {
            for (Filter filter : filters) {
                this.addFilter(filter);
            }
        }
    }

    protected FilterSet(Config config) {
        this.id = config.id();
        this.filters.addAll(config.filters());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof UniqueFilter && ((FilterSet) obj).id.equals(this.id);
    }

    @Override
    public FilterResult filter(Object... objects) {
        FilterResult def = FilterResult.ABSTAIN;

        for (Filter filter : this.filters) {
            FilterResult result = filter.filter(objects);
            if (!result.equals(def)) {
                return result;
            }
        }

        return def;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    public boolean addFilter(Filter filter) {
        return this.filters.add(Objects.requireNonNull(filter, "filter cannot be null"));
    }

    public List<Filter> getFilters() {
        return new ArrayList<>(this.filters);
    }

    public boolean isEmpty() {
        return this.filters.isEmpty();
    }

    public boolean removeFilter(Filter filter) {
        return this.filters.remove(filter);
    }

    public interface Config extends IGameConfig<FilterSet>, Unique {
        default List<Filter> filters() { return Collections.emptyList(); }

        @Override
        default FilterSet create(Game game) {
            return new FilterSet(this);
        }
    }
}
