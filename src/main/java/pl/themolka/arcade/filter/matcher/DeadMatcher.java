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

import org.bukkit.entity.Player;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.session.ArcadePlayer;

public class DeadMatcher extends ConfigurableMatcher<Boolean> {
    protected DeadMatcher(Config config) {
        super(config.value().get());
    }

    @Override
    public boolean find(Object object) {
        if (object instanceof Boolean) {
            return this.matches((Boolean) object);
        } else if (object instanceof ArcadePlayer) {
            return this.matches((ArcadePlayer) object);
        } else if (object instanceof GamePlayer) {
            return this.matches((GamePlayer) object);
        } else if (object instanceof Player) {
            return this.matches((Player) object);
        }

        return false;
    }

    public boolean matches(ArcadePlayer player) {
        return player != null && this.matches(player.getGamePlayer());
    }

    public boolean matches(GamePlayer player) {
        return player != null && this.matches(player.isDead());
    }

    public boolean matches(Player bukkit) {
        return bukkit != null && this.matches(bukkit.isDead());
    }

    @NestedParserName("dead")
    @Produces(Config.class)
    public static class MatcherParser extends BaseMatcherParser<Config> implements InstallableParser {
        private Parser<Boolean> deadParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.deadParser = context.type(Boolean.class);
        }

        @Override
        protected Result<Config> parseNode(Node node, String name, String value) throws ParserException {
            boolean dead = this.deadParser.parseWithDefinition(node, name, value).orDefault(true);

            return Result.fine(node, name, value, new Config() {
                public Ref<Boolean> value() { return Ref.ofProvided(dead); }
            });
        }
    }

    public interface Config extends ConfigurableMatcher.Config<DeadMatcher, Boolean> {
        @Override
        default DeadMatcher create(Game game, Library library) {
            return new DeadMatcher(this);
        }
    }
}
