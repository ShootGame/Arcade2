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
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

public class ClearInventoryContent implements KitContent<Boolean> {
    private final boolean result;

    protected ClearInventoryContent(Config config) {
        this.result = config.result().getOrDefault(false);
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.test(player);
    }

    @Override
    public void apply(GamePlayer player) {
        player.getPlayer().clearInventory(this.result);
    }

    @Override
    public Boolean getResult() {
        return this.result;
    }

    @NestedParserName({"clear-inventory", "clearinventory", "clear"})
    @Produces(Config.class)
    public static class ContentParser extends BaseContentParser<Config>
                                      implements InstallableParser {
        private Parser<Boolean> armorParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.armorParser = context.type(Boolean.class);
        }

        @Override
        protected Result<Config> parseNode(Node node, String name, String value) throws ParserException {
            boolean armor = this.armorParser.parse(node.property("armor", "full")).orDefault(false);

            return Result.fine(node, name, value, new Config() {
                public Ref<Boolean> result() { return Ref.ofProvided(armor); }
            });
        }
    }

    public interface Config extends KitContent.Config<ClearInventoryContent, Boolean> {
        @Override
        default ClearInventoryContent create(Game game, Library library) {
            return new ClearInventoryContent(this);
        }
    }
}
