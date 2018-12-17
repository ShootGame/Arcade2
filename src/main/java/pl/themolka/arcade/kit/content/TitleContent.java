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
import pl.themolka.arcade.session.PlayerTitle;

public class TitleContent implements RemovableKitContent<PlayerTitle> {
    private final PlayerTitle result;

    protected TitleContent(Config config) {
        this.result = config.result().getOrDefault(this.defaultValue());
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void attach(GamePlayer player, PlayerTitle value) {
        if (value != null) {
            value.apply(player);
        } else {
            player.getBukkit().hideTitle();
        }
    }

    @Override
    public PlayerTitle defaultValue() {
        return Config.DEFAULT_TITLE;
    }

    @Override
    public PlayerTitle getResult() {
        return this.result;
    }

    @NestedParserName("title")
    @Produces(Config.class)
    public static class ContentParser extends BaseRemovableContentParser<Config>
                                      implements InstallableParser {
        private Parser<PlayerTitle> titleParser;

        @Override
        public void install(ParserLibrary library) throws ParserNotSupportedException {
            super.install(library);
            this.titleParser = library.type(PlayerTitle.class);
        }

        @Override
        protected Result<Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
            if (this.reset(context, node)) {
                return Result.fine(node, name, value, new Config() {
                    public Ref<PlayerTitle> result() { return Ref.empty(); }
                });
            }

            PlayerTitle title = this.titleParser.parseWithDefinition(context, node, name, value).orFail();

            return Result.fine(node, name, value, new Config() {
                public Ref<PlayerTitle> result() { return Ref.ofProvided(title); }
            });
        }
    }

    public interface Config extends RemovableKitContent.Config<TitleContent, PlayerTitle> {
        PlayerTitle DEFAULT_TITLE = null;

        @Override
        default TitleContent create(Game game, Library library) {
            return new TitleContent(this);
        }
    }
}
