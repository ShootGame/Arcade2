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

public class FlyContent implements RemovableKitContent<Boolean> {
    private final boolean result;

    public FlyContent(Config config) {
        this.result = config.result().getOrDefault(this.defaultValue());
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void attach(GamePlayer player, Boolean value) {
        player.getBukkit().setFlying(value);
    }

    @Override
    public Boolean defaultValue() {
        return Config.DEFAULT_FLY;
    }

    @Override
    public Boolean getResult() {
        return this.result;
    }

    @NestedParserName({"fly", "flying"})
    @Produces(Config.class)
    public static class ContentParser extends BaseRemovableContentParser<Config>
                                      implements InstallableParser {
        private Parser<Boolean> flyParser;

        @Override
        public void install(ParserLibrary library) throws ParserNotSupportedException {
            super.install(library);
            this.flyParser = library.type(Boolean.class);
        }

        @Override
        protected Result<Config> parsePrimitive(Context context, Node node, String name, String value) throws ParserException {
            if (this.reset(context, node)) {
                return Result.fine(node, name, value, new Config() {
                    public Ref<Boolean> result() { return Ref.empty(); }
                });
            }

            boolean fly = this.flyParser.parseWithDefinition(context, node, name, value).orFail();

            return Result.fine(node, name, value, new Config() {
                public Ref<Boolean> result() { return Ref.ofProvided(fly); }
            });
        }
    }

    public interface Config extends RemovableKitContent.Config<FlyContent, Boolean> {
        boolean DEFAULT_FLY = false;

        @Override
        default FlyContent create(Game game, Library library) {
            return new FlyContent(this);
        }
    }
}
