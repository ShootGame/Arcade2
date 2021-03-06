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

package pl.themolka.arcade.filter.matcher;

import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

public class SpawnReasonMatcher extends ConfigurableMatcher<SpawnReason> {
    protected SpawnReasonMatcher(Config config) {
        super(config.value().get());
    }

    @Override
    public boolean find(Object object) {
        if (object instanceof SpawnReason) {
            return this.matches((SpawnReason) object);
        } else if (object instanceof CreatureSpawnEvent) {
            return this.matches((CreatureSpawnEvent) object);
        }

        return false;
    }

    public boolean matches(CreatureSpawnEvent event) {
        return event != null && this.matches(event.getSpawnReason());
    }

    @NestedParserName({"spawn-reason", "spawnreason"})
    @Produces(Config.class)
    public static class MatcherParser extends BaseMatcherParser<Config>
                                      implements InstallableParser {
        private Parser<SpawnReason> reasonParser;

        @Override
        public void install(ParserLibrary library) throws ParserNotSupportedException {
            super.install(library);
            this.reasonParser = library.type(SpawnReason.class);
        }

        @Override
        protected Result<Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
            SpawnReason reason = this.reasonParser.parseWithDefinition(context, node, name, value).orFail();

            return Result.fine(node, name, value, new Config() {
                public Ref<SpawnReason> value() { return Ref.ofProvided(reason); }
            });
        }
    }

    public interface Config extends ConfigurableMatcher.Config<SpawnReasonMatcher, SpawnReason> {
        @Override
        default SpawnReasonMatcher create(Game game, Library library) {
            return new SpawnReasonMatcher(this);
        }
    }
}
