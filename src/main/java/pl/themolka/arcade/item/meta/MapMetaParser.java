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
import org.bukkit.inventory.meta.MapMeta;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;

@Produces(MapMeta.class)
class MapMetaParser extends ItemMetaParser.Nested<MapMeta>
                    implements InstallableParser {
    private Parser<Boolean> scalingParser;
    private Parser<String> locationNameParser;
    private Parser<Color> colorParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.scalingParser = library.type(Boolean.class);
        this.locationNameParser = library.type(String.class);
        this.colorParser = library.type(Color.class);
    }

    @Override
    public MapMeta parse(Context context, Node root, ItemStack itemStack, MapMeta itemMeta) throws ParserException {
        Node node = root.firstChild("map");
        if (node != null) {
            Property scaling = node.property("scaling");
            if (scaling != null) {
                itemMeta.setScaling(this.scalingParser.parse(context, scaling).orFail());
            }

            Property locationName = node.property("location-name");
            if (locationName != null) {
                itemMeta.setLocationName(this.locationNameParser.parse(context, locationName).orFail());
            }

            Property color = node.property("color");
            if (color != null) {
                itemMeta.setColor(this.colorParser.parse(context, color).orFail());
            }
        }

        return itemMeta;
    }
}
