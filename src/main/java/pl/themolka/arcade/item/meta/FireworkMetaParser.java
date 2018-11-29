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

import org.bukkit.FireworkEffect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;

@Produces(FireworkMeta.class)
class FireworkMetaParser extends ItemMetaParser.Nested<FireworkMeta>
                         implements InstallableParser {
    private Parser<Boolean> clearEffects;
    private Parser<Integer> powerParser;
    private Parser<FireworkEffect> fireworkEffectParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.clearEffects = context.type(Boolean.class);
        this.powerParser = context.type(Integer.class);
        this.fireworkEffectParser = context.type(FireworkEffect.class);
    }

    @Override
    public FireworkMeta parse(Node root, ItemStack itemStack, FireworkMeta itemMeta) throws ParserException {
        Node node = root.firstChild("firework");
        if (node != null) {
            Property clearEffects = node.property("clear-effects");
            if (clearEffects != null && this.clearEffects.parse(clearEffects).orDefault(false)) {
                itemMeta.clearEffects();
            }

            Property power = node.property("power");
            if (power != null) {
                int powerInt = this.powerParser.parse(power).orFail();
                if (powerInt > 127) {
                    throw this.fail(power, "Power cannot be greater than 127");
                } else if (powerInt < 0) {
                    throw this.fail(power, "Power cannot be smaller than 0");
                }

                itemMeta.setPower(powerInt);
            }

            for (Node effect : node.children("effect", "firework-effect", "fireworkeffect")) {
                itemMeta.addEffect(this.fireworkEffectParser.parse(effect).orFail());
            }
        }

        return itemMeta;
    }
}
