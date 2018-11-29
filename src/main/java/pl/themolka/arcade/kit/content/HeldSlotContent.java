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

import org.bukkit.inventory.PlayerInventory;
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

public class HeldSlotContent extends BaseInventoryContent<Integer> {
    public static final int MIN_VALUE = 0;
    public static final int MAX_VALUE = net.minecraft.server.PlayerInventory.getHotbarSize() - 1;

    public static boolean testValue(int value) {
        return value >= MIN_VALUE && value <= MAX_VALUE;
    }

    protected HeldSlotContent(Config config) {
        super(config);
    }

    @Override
    public void apply(GamePlayer player, PlayerInventory inventory) {
        inventory.setHeldItemSlot(this.getResult());
    }

    @NestedParserName({"held-slot", "heldslot", "held-item", "helditem", "held", "slot"})
    @Produces(Config.class)
    public static class ContentParser extends BaseContentParser<Config>
                                      implements InstallableParser {
        private Parser<Integer> slotParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.slotParser = context.type(Integer.class);
        }

        @Override
        protected Result<Config> parsePrimitive(Node node, String name, String value) throws ParserException {
            int slot = this.slotParser.parseWithDefinition(node, name, value).orFail();
            if (slot < MIN_VALUE || slot > MAX_VALUE) {
                throw this.fail(node, name, value, "Unknown slot number (not in " + MIN_VALUE + "-" + MAX_VALUE + " range)");
            }

            return Result.fine(node, name, value, new Config() {
                public Ref<Integer> result() { return Ref.ofProvided(slot); }
            });
        }
    }

    public interface Config extends BaseInventoryContent.Config<HeldSlotContent, Integer> {
        @Override
        default HeldSlotContent create(Game game, Library library) {
            return new HeldSlotContent(this);
        }
    }
}
