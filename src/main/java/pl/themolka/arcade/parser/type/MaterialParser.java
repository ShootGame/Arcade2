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

package pl.themolka.arcade.parser.type;

import org.bukkit.Material;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.Set;

@Produces(Material.class)
public class MaterialParser extends ElementParser<Material>
                            implements InstallableParser {
    public static final String[] MATERIAL_ELEMENT_NAMES = {"material", "type", "kind", "of"};

    private Parser<Material> materialParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.materialParser = library.enumType(Material.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("a material type");
    }

    @Override
    protected Result<Material> parseElement(Context context, Element element, String name, String value) throws ParserException {
        Material material = Material.matchMaterial(value, false);
        if (material == null) {
            material = Material.matchMaterial(value, true);
        }

        if (material != null) {
            return Result.fine(element, name, value, material);
        }

        return this.materialParser.parseWithDefinition(context, element, name, value);
    }
}
