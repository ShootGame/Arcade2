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

package pl.themolka.arcade.gamerule;

import org.bukkit.World;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.IGameModuleConfig;

import java.util.LinkedHashSet;
import java.util.Set;

public class GameRulesGame extends GameModule {
    private final Set<GameRule> rules = new LinkedHashSet<>();

    protected GameRulesGame(Config config) {
        this.rules.addAll(config.rules().get());
    }

    @Override
    public void onEnable() {
        World world = this.getGame().getWorld();
        for (GameRule rule : this.rules) {
            GameRuleType type = GameRuleType.byKey(rule.getKey());
            if (type != null && !type.isApplicable()) {
                continue;
            }

            if (rule.isApplicable()) {
                rule.apply(world);
            }
        }
    }

    public Set<GameRule> getRules() {
        return new LinkedHashSet<>(this.rules);
    }

    public interface Config extends IGameModuleConfig<GameRulesGame> {
        Ref<Set<GameRule>> rules();

        @Override
        default GameRulesGame create(Game game, Library library) {
            return new GameRulesGame(this);
        }
    }
}
