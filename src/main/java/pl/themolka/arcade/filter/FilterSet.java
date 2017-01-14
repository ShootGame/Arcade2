package pl.themolka.arcade.filter;

import pl.themolka.arcade.util.StringId;

import java.util.ArrayList;
import java.util.List;

public class FilterSet implements Filter, StringId {
    private final List<Filter> filters = new ArrayList<>();
    private final String id;

    public FilterSet(String id) {
        this(id, (Filter[]) null);
    }

    public FilterSet(String id, Filter... filters) {
        this.id = id;

        if (filters != null) {
            for (Filter filter : filters) {
                this.addFilter(filter);
            }
        }
    }

    @Override
    public FilterResult filter(Object... objects) {
        for (Filter filter : this.getFilters()) {
            FilterResult result = filter.filter(objects);
            if (!result.equals(FilterResult.ABSTAIN)) {
                return result;
            }
        }

        return FilterResult.ABSTAIN;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public void addFilter(Filter filter) {
        this.filters.add(filter);
    }

    public List<Filter> getFilters() {
        return this.filters;
    }

    public boolean isEmpty() {
        return this.filters.isEmpty();
    }

    public void removeFilter(Filter filter) {
        this.filters.remove(filter);
    }
}
