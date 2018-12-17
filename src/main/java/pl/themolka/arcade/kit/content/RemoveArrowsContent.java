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

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

public class RemoveArrowsContent implements BaseVoidKitContent {
    public static final int FINAL_COUNT = 0;

    protected RemoveArrowsContent() {
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void apply(GamePlayer player) {
        player.getBukkit().setArrowsStuck(FINAL_COUNT);
    }

    @NestedParserName({"remove-arrows", "removearrows"})
    @Produces(Config.class)
    public static class ContentParser extends BaseContentParser<Config> {
        @Override
        protected Result<Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
            return Result.fine(node, name, value, new Config() {});
        }
    }

    public interface Config extends BaseVoidKitContent.Config<RemoveArrowsContent> {
        @Override
        default RemoveArrowsContent create(Game game, Library library) {
            return new RemoveArrowsContent();
        }
    }
}
