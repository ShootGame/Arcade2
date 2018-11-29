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

import org.bukkit.GameMode;
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

public class GameModeContent implements KitContent<GameMode>  {
    private final GameMode result;

    protected GameModeContent(Config config) {
        this.result = config.result().get();
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void apply(GamePlayer player) {
        player.getBukkit().setGameMode(this.getResult());
    }

    @Override
    public GameMode getResult() {
        return this.result;
    }

    @NestedParserName({"gamemode", "game-mode", "gm"})
    @Produces(Config.class)
    public static class ContentParser extends BaseContentParser<Config>
                                      implements InstallableParser {
        private Parser<GameMode> gameModeParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.gameModeParser = context.type(GameMode.class);
        }

        @Override
        protected Result<Config> parsePrimitive(Node node, String name, String value) throws ParserException {
            GameMode gameMode = this.gameModeParser.parseWithDefinition(node, name, value).orFail();

            return Result.fine(node, name, value, new Config() {
                public Ref<GameMode> result() { return Ref.ofProvided(gameMode); }
            });
        }
    }

    public interface Config extends KitContent.Config<GameModeContent, GameMode> {
        GameMode DEFAULT_GAME_MODE = GameMode.SURVIVAL;

        @Override
        default GameModeContent create(Game game, Library library) {
            return new GameModeContent(this);
        }
    }
}
