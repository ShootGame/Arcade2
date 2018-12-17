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

package pl.themolka.arcade.item.meta;

import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;

@Produces(PotionMeta.class)
class PotionMetaParser extends ItemMetaParser.Nested<PotionMeta>
                       implements InstallableParser {
    private Parser<PotionType> typeParser;
    private Parser<Boolean> extendedParser;
    private Parser<Boolean> upgradedParser;
    private Parser<Color> colorParser;
    private Parser<PotionEffect> potionEffectParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.typeParser = library.type(PotionType.class);
        this.extendedParser = library.type(Boolean.class);
        this.upgradedParser = library.type(Boolean.class);
        this.colorParser = library.type(Color.class);
        this.potionEffectParser = library.type(PotionEffect.class);
    }

    @Override
    public PotionMeta parse(Context context, Node root, ItemStack itemStack, PotionMeta itemMeta) throws ParserException {
        Node node = root.firstChild("potion");
        if (node != null) {
            Property type = node.property("type", "of");
            if (type != null) {
                PotionType value = this.typeParser.parse(context, type).orFail();
                boolean extended = this.extendedParser.parse(context, node.property("extended")).orDefault(false);
                boolean upgraded = this.upgradedParser.parse(context, node.property("upgraded")).orDefault(false);

                if (extended && !value.isUpgradeable()) {
                    throw this.fail(type, "Potion is not extendable");
                } else if (upgraded && !value.isUpgradeable()) {
                    throw this.fail(type, "Potion is not upgradeable");
                } else if (extended && upgraded) {
                    throw this.fail(type, "Potion cannot be both extended and upgraded");
                }

                itemMeta.setBasePotionData(new PotionData(value, extended, upgraded));
            }

            Property color = node.property("color");
            if (color != null) {
                itemMeta.setColor(this.colorParser.parse(context, color).orFail());
            }

            for (Node effect : node.children("effect", "potion-effect", "potioneffect")) {
                PotionEffect value = this.potionEffectParser.parse(context, effect).orFail();
                if (!itemMeta.hasCustomEffect(value.getType())) {
                    itemMeta.addCustomEffect(value, false);
                }
            }
        }

        return itemMeta;
    }
}
