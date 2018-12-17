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

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

public class LeggingsContent extends BaseArmorContent {
    protected LeggingsContent(Config config) {
        super(config);
    }

    @Override
    public void attach(PlayerInventory inventory, ItemStack value) {
        inventory.setLeggings(value);
    }

    @NestedParserName({"leggings", "armor-leggings", "armorleggings"})
    @Produces(Config.class)
    public static class ContentParser extends BaseRemovableContentParser<Config>
                                      implements InstallableParser {
        private Parser<ItemStack> itemStackParser;

        @Override
        public void install(ParserLibrary library) throws ParserNotSupportedException {
            super.install(library);
            this.itemStackParser = library.type(ItemStack.class);
        }

        @Override
        protected Result<Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
            if (this.reset(context, node)) {
                return Result.fine(node, name, value, new Config() {
                    public Ref<ItemStack> result() { return Ref.empty(); }
                });
            }

            ItemStack leggings = this.itemStackParser.parseWithDefinition(context, node, name, value).orFail();

            return Result.fine(node, name, value, new Config() {
                public Ref<ItemStack> result() { return Ref.ofProvided(leggings); }
            });
        }
    }

    public interface Config extends BaseArmorContent.Config<LeggingsContent> {
        @Override
        default LeggingsContent create(Game game, Library library) {
            return new LeggingsContent(this);
        }
    }
}
