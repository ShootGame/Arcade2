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

public class SprintingMatcher extends ConfigurableMatcher<Boolean> {
    protected SprintingMatcher(Config config) {
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
        return player != null && this.matches(player.getBukkit());
    }

    public boolean matches(Player bukkit) {
        return bukkit != null && this.matches(bukkit.isSprinting());
    }

    @NestedParserName("sprinting")
    @Produces(Config.class)
    public static class MatcherParser extends BaseMatcherParser<Config> implements InstallableParser {
        private Parser<Boolean> sprintingParser;

        @Override
        public void install(ParserLibrary library) throws ParserNotSupportedException {
            super.install(library);
            this.sprintingParser = library.type(Boolean.class);
        }

        @Override
        protected Result<Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
            boolean sprinting = this.sprintingParser.parseWithDefinition(context, node, name, value).orDefault(true);

            return Result.fine(node, name, value, new Config() {
                public Ref<Boolean> value() { return Ref.ofProvided(sprinting); }
            });
        }
    }

    public interface Config extends ConfigurableMatcher.Config<SprintingMatcher, Boolean> {
        @Override
        default SprintingMatcher create(Game game, Library library) {
            return new SprintingMatcher(this);
        }
    }
}
