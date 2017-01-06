package pl.themolka.arcade.filter;

import net.engio.mbassy.listener.Handler;
import org.jdom2.Element;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GameStartEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FiltersGame extends GameModule {
    private final Map<String, FilterSet> filters = new HashMap<>();

    @Handler(priority = Priority.HIGHER)
    public void onGameStart(GameStartEvent event) {
        this.parseFilters();
    }

    public void addFilter(FilterSet filter) {
        this.filters.put(filter.getId(), filter);
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

    private void parseFilters() {
        for (Element baseElement : this.getSettings().getChildren("filter")) {
            String id = baseElement.getAttributeValue("id");
            if (id == null) {
                continue;
            }

            FilterSet set = new FilterSet(id);
            for (Element filterElement : baseElement.getChildren()) {
                Filter filter = XMLFilter.parse(this.getPlugin(), filterElement);
                if (filter != null) {
                    set.addFilter(filter);
                }
            }

            if (!set.isEmpty()) {
                this.addFilter(set);
            }
        }
    }
}
