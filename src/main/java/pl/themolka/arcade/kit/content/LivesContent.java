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
import pl.themolka.arcade.life.LivesGame;
import pl.themolka.arcade.life.LivesModule;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

public class LivesContent implements RemovableKitContent<Integer> {
    private final int result;

    protected LivesContent(Config config) {
        this.result = config.result().getOrDefault(this.defaultValue());
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.test(player);
    }

    @Override
    public void attach(GamePlayer player, Integer value) {
        LivesGame module = (LivesGame) player.getGame().getModule(LivesModule.class);
        if (module != null && module.isEnabled()) {
            module.addLives(player, value);
        }
    }

    @Override
    public Integer defaultValue() {
        return Config.DEFAULT_LIVES;
    }

    @Override
    public Integer getResult() {
        return this.result;
    }

    @NestedParserName({"lives", "life"})
    @Produces(Config.class)
    public static class ContentParser extends BaseRemovableContentParser<Config>
                                      implements InstallableParser {
        private Parser<Integer> livesParser;

        @Override
        public void install(ParserLibrary library) throws ParserNotSupportedException {
            super.install(library);
            this.livesParser = library.type(Integer.class);
        }

        @Override
        protected Result<Config> parsePrimitive(Context context, Node node, String name, String value) throws ParserException {
            if (this.reset(context, node)) {
                return Result.fine(node, name, value, new Config() {
                    public Ref<Integer> result() { return Ref.empty(); }
                });
            }

            int lives = this.livesParser.parseWithDefinition(context, node, name, value).orFail();
            if (lives == 0) {
                throw this.fail(node, name, value, "No lives to increment or decrement");
            }

            return Result.fine(node, name, value, new Config() {
                public Ref<Integer> result() { return Ref.ofProvided(lives); }
            });
        }
    }

    public interface Config extends RemovableKitContent.Config<LivesContent, Integer> {
        int DEFAULT_LIVES = +1;

        @Override
        default LivesContent create(Game game, Library library) {
            return new LivesContent(this);
        }
    }
}
