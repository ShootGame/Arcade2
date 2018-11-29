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

package pl.themolka.arcade.life;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.config.Unique;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.Filters;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.PlayerApplicable;
import pl.themolka.arcade.kit.Kit;
import pl.themolka.arcade.util.StringId;

public class KillReward implements PlayerApplicable, StringId {
    private final Filter filter;
    private final String id;
    private final Kit kit;

    protected KillReward(Game game, IGameConfig.Library library, Config config) {
        this.filter = Filters.secure(library.getOrDefine(game, config.filter().getIfPresent()));
        this.id = config.id();
        this.kit = library.getOrDefine(game, config.kit().get());
    }

    @Override
    public void apply(GamePlayer player) {
        if (this.canReward(player)) {
            this.rewardPlayer(player);
        }
    }

    @Override
    public String getId() {
        return this.id;
    }

    public boolean canReward(GamePlayer player) {
        return player != null && player.isOnline() && this.filter.filter(player).isNotFalse();
    }

    public Filter getFilter() {
        return this.filter;
    }

    public Kit getKit() {
        return this.kit;
    }

    public boolean rewardPlayer(GamePlayer player) {
        if (player != null && player.isOnline()) {
            Kit kit = this.getKit();

            if (kit != null) {
                kit.apply(player);
                return true;
            }
        }

        return false;
    }

    public interface Config extends IGameConfig<KillReward>, Unique {
        default Ref<Filter.Config<?>> filter() { return Ref.empty(); }
        default Ref<Kit.Config> kit() { return Ref.empty(); }

        @Override
        default KillReward create(Game game, Library library) {
            return new KillReward(game, library, this);
        }
    }
}
