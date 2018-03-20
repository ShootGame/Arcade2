package pl.themolka.arcade.game;

import java.util.LinkedHashSet;
import java.util.Set;

public class VisibilityManager implements PlayerVisibilityFilter {
    private final Set<PlayerVisibilityFilter> filters = new LinkedHashSet<>();

    @Override
    public boolean canSee(GamePlayer viewer, GamePlayer target) {
        if (!viewer.isOnline() || !target.isOnline()) {
            return false;
        }

        boolean def = PlayerVisibilityFilter.DEFAULT.canSee(viewer, target);
        for (PlayerVisibilityFilter filter : this.filters) {
            boolean value = filter.canSee(viewer, target);
            if (value != def) {
                return value;
            }
        }

        return def;
    }

    public boolean addFilter(PlayerVisibilityFilter filter) {
        return filter != null && !filter.equals(this) && this.filters.add(filter);
    }

    public boolean removeFilter(PlayerVisibilityFilter filter) {
        return filter != null && this.filters.remove(filter);
    }
}
