package pl.themolka.arcade.filter;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.IGameModuleConfig;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class FiltersGame extends GameModule {
    private final Map<String, UniqueFilter> filters = new LinkedHashMap<>();

    protected FiltersGame(Game game, IGameConfig.Library library, Config config) {
        for (FilterSet.Config filter : config.filterSets().get()) {
            this.filters.put(filter.id(), library.getOrDefine(game, filter));
        }
    }

    public void addFilter(FilterSet filter) {
        this.filters.put(filter.getId(), filter);
    }

    public Filter filterOrDefault(String id, Filter def) {
        if (id != null) {
            UniqueFilter result = this.getFilter(id.trim());
            if (result != null) {
                return result;
            }
        }

        return def;
    }

    public UniqueFilter getFilter(String id) {
        return this.getFilter(id, null);
    }

    public UniqueFilter getFilter(String id, UniqueFilter def) {
        return this.filters.getOrDefault(id, def);
    }

    public Set<String> getFilterIds() {
        return this.filters.keySet();
    }

    public Collection<UniqueFilter> getFilters() {
        return this.filters.values();
    }

    public void removeFilter(UniqueFilter filter) {
        this.removeFilter(filter.getId());
    }

    public void removeFilter(String id) {
        this.filters.remove(id);
    }

    public interface Config extends IGameModuleConfig<FiltersGame> {
        Ref<Set<FilterSet.Config>> filterSets();

        @Override
        default FiltersGame create(Game game, Library library) {
            return new FiltersGame(game, library, this);
        }
    }
}
