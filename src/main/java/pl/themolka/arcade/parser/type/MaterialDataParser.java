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
import org.bukkit.material.MaterialData;
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

@Produces(MaterialData.class)
public class MaterialDataParser extends ElementParser<MaterialData>
                                implements InstallableParser {
    private Parser<Material> materialParser;
    private Parser<Byte> dataParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.materialParser = library.type(Material.class);
        this.dataParser = library.type(Byte.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("a material data type");
    }

    @Override
    protected Result<MaterialData> parseElement(Context context, Element element, String name, String value) throws ParserException {
        String[] split = value.split(":", 2);

        Material material = this.materialParser.parseWithDefinition(context, element, name, split[0]).orFail();
        byte data = split.length > 1 ? this.dataParser.parseWithDefinition(context, element, name, split[1]).orFail()
                                     : 0;

        return Result.fine(element, name, value, new MaterialData(material, data));
    }
}
