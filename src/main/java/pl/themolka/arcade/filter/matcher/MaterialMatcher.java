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

package pl.themolka.arcade.filter.matcher;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
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

public class MaterialMatcher extends ConfigurableMatcher<MaterialData> {
    protected MaterialMatcher(Config config) {
        super(config.value().get());
    }

    @Override
    public boolean find(Object object) {
        if (object instanceof MaterialData) {
            return this.matches((MaterialData) object);
        } else if (object instanceof Block) {
            return this.matches((Block) object);
        } else if (object instanceof BlockState) {
            return this.matches((BlockState) object);
        } else if (object instanceof ItemStack) {
            return this.matches((ItemStack) object);
        } else if (object instanceof Location) {
            return this.matches((Location) object);
        } else if (object instanceof Material) {
            return this.matches((Material) object);
        }

        return false;
    }

    public boolean matches(Block block) {
        return block != null && this.matches(block.getState());
    }

    public boolean matches(BlockState blockState) {
        return blockState != null && this.matches(blockState.getData());
    }

    public boolean matches(ItemStack itemStack) {
        return itemStack != null && this.matches(itemStack.getData());
    }

    public boolean matches(Location location) {
        return location != null && this.matches(location.getBlock());
    }

    public boolean matches(Material material) {
        return material != null && this.matches(new MaterialData(material));
    }

    @NestedParserName("material")
    @Produces(Config.class)
    public static class MatcherParser extends BaseMatcherParser<Config>
                                      implements InstallableParser {
        private Parser<MaterialData> materialParser;

        @Override
        public void install(ParserLibrary library) throws ParserNotSupportedException {
            super.install(library);
            this.materialParser = library.type(MaterialData.class);
        }

        @Override
        protected Result<Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
            MaterialData material = this.materialParser.parseWithDefinition(context, node, name, value).orFail();

            return Result.fine(node, name, value, new Config() {
                public Ref<MaterialData> value() { return Ref.ofProvided(material); }
            });
        }
    }

    public interface Config extends ConfigurableMatcher.Config<MaterialMatcher, MaterialData> {
        @Override
        default MaterialMatcher create(Game game, Library library) {
            return new MaterialMatcher(this);
        }
    }
}
