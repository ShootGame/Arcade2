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

package pl.themolka.arcade.objective;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.IGameModuleConfig;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.match.MatchGame;
import pl.themolka.arcade.match.MatchModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ObjectivesGame extends GameModule {
    private final Map<String, Objective> byId = new HashMap<>();

    protected ObjectivesGame(Game game, IGameConfig.Library library, Config config) {
        for (Objective.Config<?> objective : config.objectives().get()) {
            this.byId.put(objective.id(), library.getOrDefine(game, objective));
        }
    }

    @Override
    public void onEnable() {
        Game game = this.getGame();
        Match match = ((MatchGame) game.getModule(MatchModule.class)).getMatch();
        Set<Objective> objectives = new HashSet<>(this.byId.values());

        for (Objective objective : objectives) {
            objective.injectParticipatorResolver(match);
            objective.registerEventListeners(this, true);
        }

        Set<Object> listeners = new HashSet<>();
        for (ObjectiveManifest manifest : ObjectiveManifest.manifests) {
            manifest.onEnable(game, objectives, listeners);
        }

        for (Object listener : listeners) {
            this.registerListenerObject(listener);
        }
    }

    public Objective getObjective(String id) {
        return this.byId.get(Objects.requireNonNull(id, "id cannot be null"));
    }

    public List<Objective> getObjectives() {
        return new ArrayList<>(this.byId.values());
    }

    public interface Config extends IGameModuleConfig<ObjectivesGame> {
        Ref<Set<Objective.Config<?>>> objectives();

        @Override
        default ObjectivesGame create(Game game, Library library) {
            return new ObjectivesGame(game, library, this);
        }
    }
}
