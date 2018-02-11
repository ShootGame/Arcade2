package pl.themolka.arcade.filter;

import pl.themolka.arcade.game.GameModule;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class FiltersGame extends GameModule {
    private final Map<String, FilterSet> filters;

    public FiltersGame(Map<String, FilterSet> filters) {
        this.filters = filters;
    }

    public void addFilter(FilterSet filter) {
        this.filters.put(filter.getId(), filter);
    }

    public Filter filterOrDefault(String id, Filter def) {
        if (id != null) {
            FilterSet result = this.getFilter(id.trim());
            if (result != null) {
                return result;
            }
        }

        return def;
    }

    public FilterSet getFilter(String id) {
        return this.getFilter(id, null);
    }

    public FilterSet getFilter(String id, FilterSet def) {
        return this.filters.getOrDefault(id, def);
    }

    public Set<String> getFilterIds() {
        return this.filters.keySet();
    }

    public Collection<FilterSet> getFilters() {
        return this.filters.values();
    }

    public void removeFilter(FilterSet filter) {
        this.removeFilter(filter.getId());
    }

    public void removeFilter(String id) {
        this.filters.remove(id);
    }
}
