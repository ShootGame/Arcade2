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

import com.google.common.collect.ImmutableSet;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserValidation;
import pl.themolka.arcade.parser.Produces;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// No registration - no @Produces production.
public class ItemMetaParser implements InstallableParser {
    private static final Set<Class<? extends Nested>> types = ImmutableSet.<Class<? extends Nested>>builder()
            .add(BannerMetaParser.class)
            .add(BookMetaParser.class)
            .add(EnchantmentStorageMetaParser.class)
            .add(FireworkMetaParser.class)
            .add(FireworkEffectMetaParser.class)
            .add(LeatherArmorMetaParser.class)
            .add(MapMetaParser.class)
            .add(PotionMetaParser.class)
            .add(SkullMetaParser.class)
            .add(SpawnEggMetaParser.class)
            .build();

    private Map<Class<? extends ItemMeta>, Nested<?>> parsers;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.parsers = new HashMap<>();
        for (Class<? extends Nested> clazz : types) {
            Produces produces = clazz.getDeclaredAnnotation(Produces.class);
            if (produces == null) {
                continue;
            }

            Class<?> metaType = produces.value();
            if (metaType == null || !ItemMeta.class.isAssignableFrom(metaType)) {
                continue;
            }

            try {
                this.parsers.put((Class<? extends ItemMeta>) metaType, clazz.newInstance());
            } catch (ReflectiveOperationException ignored) {
            }
        }

        for (Nested<?> parser : this.parsers.values()) {
            parser.install(context);
        }
    }

    public ItemMeta parse(Node node, ItemStack itemStack, ItemMeta itemMeta) throws ParserException {
        Nested parser = this.parsers.get(itemMeta.getClass());
        if (parser != null) {
            itemMeta = parser.parse(node, itemStack, itemMeta);
        }

        return itemMeta;
    }

    /**
     * Base class for all {@link ItemMeta} parsers.
     */
    static abstract class Nested<T extends ItemMeta> extends ParserValidation
                                                     implements InstallableParser {
        public abstract T parse(Node root, ItemStack itemStack, T itemMeta) throws ParserException;
    }
}
