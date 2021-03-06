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

package pl.themolka.arcade.kit.content;

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
import pl.themolka.arcade.session.PlayerLevel;

public class GiveLevelContent implements KitContent<PlayerLevel> {
    public static boolean testValue(int value) {
        return value != 0;
    }

    private final PlayerLevel result;

    protected GiveLevelContent(Config config) {
        this.result = config.result().get();
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void apply(GamePlayer player) {
        this.getResult().applyIncremental(player);
    }

    @Override
    public PlayerLevel getResult() {
        return this.result;
    }

    @NestedParserName({"give-level", "givelevel", "give-levels", "givelevels", "give-lvl", "givelvl",
                       "take-level", "takelevel", "take-levels", "takelevels", "take-lvl", "takelvl"})
    @Produces(Config.class)
    public static class ContentParser extends BaseContentParser<Config>
                                      implements InstallableParser {
        private Parser<PlayerLevel> levelParser;

        @Override
        public void install(ParserLibrary library) throws ParserNotSupportedException {
            super.install(library);
            this.levelParser = library.type(PlayerLevel.class);
        }

        @Override
        protected Result<Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
            PlayerLevel level = this.levelParser.parseWithDefinition(context, node, name, value).orFail();

            if (name.toLowerCase().startsWith("take")) {
                level = level.negate();
            }

            final PlayerLevel finalLevel = level;
            return Result.fine(node, name, value, new Config() {
                public Ref<PlayerLevel> result() { return Ref.ofProvided(finalLevel); }
            });
        }
    }

    public interface Config extends KitContent.Config<GiveLevelContent, PlayerLevel> {
        @Override
        default GiveLevelContent create(Game game, Library library) {
            return new GiveLevelContent(this);
        }
    }
}
