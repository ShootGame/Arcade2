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
