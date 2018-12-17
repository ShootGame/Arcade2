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

public class ItemStackContent extends BaseInventoryContent<ItemStack>
                              implements BaseModeContent {
    // armor
    public static final int SLOT_HELMET = 103;
    public static final int SLOT_CHESTPLATE = 102;
    public static final int SLOT_LEGGNINGS = 101;
    public static final int SLOT_BOOTS = 100;

    private final Mode mode;
    private final Integer slot;

    protected ItemStackContent(Config config) {
        super(config);

        this.mode = config.mode();
        this.slot = config.slot().getIfPresent();
    }

    @Override
    public void apply(GamePlayer player, PlayerInventory inventory) {
        if (this.give()) {
            if (this.hasSlot()) {
                inventory.setItem(this.getSlot(), this.getResult());
            } else {
                inventory.addItem(this.getResult());
            }
        } else if (this.take()) {
            inventory.remove(this.getResult());
        }
    }

    @Override
    public boolean give() {
        return this.mode.equals(Mode.GIVE);
    }

    @Override
    public boolean take() {
        return this.mode.equals(Mode.TAKE);
    }

    public int getSlot() {
        return this.slot;
    }

    public boolean hasSlot() {
        return this.slot != null;
    }

    @NestedParserName({"item", "item-stack", "itemstack"})
    @Produces(Config.class)
    public static class ContentParser extends BaseContentParser<Config>
                                      implements InstallableParser {
        private Parser<ItemStack> itemStackParser;
        private Parser<Mode> modeParser;
        private Parser<Integer> slotParser;

        @Override
        public void install(ParserLibrary library) throws ParserNotSupportedException {
            super.install(library);
            this.itemStackParser = library.type(ItemStack.class);
            this.modeParser = library.type(Mode.class);
            this.slotParser = library.type(Integer.class);
        }

        @Override
        protected Result<Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
            ItemStack itemStack = this.itemStackParser.parseWithDefinition(context, node, name, value).orFail();
            BaseModeContent.Mode mode = this.modeParser.parseWithDefinition(context, node, name, value).orDefault(Config.DEFAULT_MODE);
            Integer slot = this.slotParser.parse(context, node.property("slot")).orDefaultNull();

            return Result.fine(node, name, value, new Config() {
                public Ref<ItemStack> result() { return Ref.ofProvided(itemStack); }
                public BaseModeContent.Mode mode() { return mode; }
                public Ref<Integer> slot() { return slot != null ? Ref.ofProvided(slot) : Ref.empty(); }
            });
        }
    }

    public interface Config extends BaseInventoryContent.Config<ItemStackContent, ItemStack>,
                                    BaseModeContent.Config {
        default Ref<Integer> slot() { return Ref.empty(); }

        @Override
        default ItemStackContent create(Game game, Library library) {
            return new ItemStackContent(this);
        }
    }
}
