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

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.GamePlayerSnapshot;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.session.ArcadePlayer;

public class ParticipatingMatcher extends ConfigurableMatcher<Boolean> {
    protected ParticipatingMatcher(Config config) {
        super(config.value().get());
    }

    @Override
    public boolean find(Object object) {
        if (object instanceof Boolean) {
            return this.matches((Boolean) object);
        } else if (object instanceof GamePlayer) {
            return this.matches((GamePlayer) object);
        } else if (object instanceof ArcadePlayer) {
            return this.matches((ArcadePlayer) object);
        } else if (object instanceof GamePlayerSnapshot) {
            return this.matches((GamePlayerSnapshot) object);
        }

        return false;
    }

    public boolean matches(GamePlayer player) {
        return player != null && this.matches(player.isParticipating());
    }

    public boolean matches(ArcadePlayer player) {
        return player != null && this.matches(player.getGamePlayer());
    }

    public boolean matches(GamePlayerSnapshot snapshot) {
        return snapshot != null && this.matches(snapshot.isParticipating());
    }

    @NestedParserName("participating")
    @Produces(Config.class)
    public static class MatcherParser extends BaseMatcherParser<Config>
                                      implements InstallableParser {
        private Parser<Boolean> participatingParser;

        @Override
        public void install(ParserLibrary library) throws ParserNotSupportedException {
            super.install(library);
            this.participatingParser = library.type(Boolean.class);
        }

        @Override
        protected Result<Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
            boolean participating = this.participatingParser.parseWithDefinition(context, node, name, value).orDefault(true);

            return Result.fine(node, name, value, new Config() {
                public Ref<Boolean> value() { return Ref.ofProvided(participating); }
            });
        }
    }

    public interface Config extends ConfigurableMatcher.Config<ParticipatingMatcher, Boolean> {
        @Override
        default ParticipatingMatcher create(Game game, Library library) {
            return new ParticipatingMatcher(this);
        }
    }
}
