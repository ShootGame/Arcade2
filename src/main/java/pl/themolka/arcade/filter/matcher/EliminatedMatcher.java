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

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.life.LivesGame;
import pl.themolka.arcade.life.LivesModule;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.session.ArcadePlayer;

public class EliminatedMatcher extends Matcher<GamePlayer> {
    protected EliminatedMatcher() {
    }

    @Override
    public boolean find(Object object) {
        if (object instanceof GamePlayer) {
            return this.matches((GamePlayer) object);
        } else if (object instanceof ArcadePlayer) {
            return this.matches((ArcadePlayer) object);
        }

        return false;
    }

    @Override
    public boolean matches(GamePlayer player) {
        if (player != null) {
            LivesGame module = (LivesGame) player.getGame().getModule(LivesModule.class);
            if (module != null) {
                return module.isEliminated(player);
            }
        }

        return false;
    }

    public boolean matches(ArcadePlayer player) {
        return player != null && this.matches(player.getGamePlayer());
    }

    @NestedParserName("eliminated")
    @Produces(Config.class)
    public static class MatcherParser extends BaseMatcherParser<Config> {
        @Override
        protected Result<Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
            return Result.fine(node, name, value, new Config() {});
        }
    }

    public interface Config extends Matcher.Config<EliminatedMatcher> {
        @Override
        default EliminatedMatcher create(Game game, Library library) {
            return new EliminatedMatcher();
        }
    }
}
