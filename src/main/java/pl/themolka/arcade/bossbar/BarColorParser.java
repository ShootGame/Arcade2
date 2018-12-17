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

package pl.themolka.arcade.bossbar;

import org.bukkit.DyeColor;
import org.bukkit.boss.BarColor;
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

@Produces(BarColor.class)
public class BarColorParser extends ElementParser<BarColor>
                            implements InstallableParser {
    private Parser<BarColor> colorParser;
    private Parser<DyeColor> dyeParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.colorParser = library.enumType(BarColor.class);
        this.dyeParser = library.type(DyeColor.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("boss bar color");
    }

    @Override
    protected Result<BarColor> parseElement(Context context, Element element, String name, String value) throws ParserException {
        DyeColor dye = this.dyeParser.parseWithDefinition(context, element, name, value).orNull();
        if (dye != null) {
            BarColor result = BossBarUtils.color(dye);

            if (result != null) {
                return Result.fine(element, name, value, result);
            }
        }

        return Result.fine(element, name, value, this.colorParser.parseWithDefinition(context, element, name, value).orFail());
    }
}
