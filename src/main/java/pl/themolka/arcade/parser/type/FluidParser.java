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
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.util.material.Fluid;

import java.util.Collections;
import java.util.Set;

@Produces(Fluid.class)
public class FluidParser extends ElementParser<Fluid>
                         implements InstallableParser {
    private Parser<Material> materialParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.materialParser = library.type(Material.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("fluid or material representing fluid");
    }

    @Override
    protected Result<Fluid> parseElement(Context context, Element element, String name, String value) throws ParserException {
        Material material = this.materialParser.parseWithDefinition(context, element, name, value).orFail();

        Fluid fluid;
        if (material.equals(Material.LAVA)) {
            fluid = Fluid.LAVA;
        } else if (material.equals(Material.WATER)) {
            fluid = Fluid.WATER;
        } else {
            throw this.fail(element, name, value, "Unknown fluid");
        }

        return Result.fine(element, name, value, fluid);
    }
}
