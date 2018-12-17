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

import org.bukkit.Color;
import org.bukkit.DyeColor;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserUtils;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Produces(Color.class)
public class ColorParser extends ElementParser<Color>
                         implements InstallableParser {
    private Parser<DyeColor> predefinedParser;
    private Parser<Integer> redParser;
    private Parser<Integer> greenParser;
    private Parser<Integer> blueParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.predefinedParser = library.type(DyeColor.class);
        this.redParser = library.type(Integer.class);
        this.greenParser = library.type(Integer.class);
        this.blueParser = library.type(Integer.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("RGB color (or a predefined color)");
    }

    @Override
    protected Result<Color> parseElement(Context context, Element element, String name, String value) throws ParserException {
        if (value.startsWith("#")) { // HEX
            try {
                java.awt.Color awtColor = java.awt.Color.decode(value);

                int red = this.color(element, name, value, "Red", awtColor.getRed());
                int green = this.color(element, name, value, "Green", awtColor.getGreen());
                int blue = this.color(element, name, value, "Blue", awtColor.getBlue());
                return Result.fine(element, name, value, Color.fromRGB(red, green, blue));
            } catch (NumberFormatException e) {
                throw this.fail(element, name, value, "Illegal hexadecimal syntax", e);
            }
        }

        List<String> array = ParserUtils.array(value, 3);
        if (array.size() == 1) { // maybe predefined
            DyeColor predefined = this.predefinedParser.parseWithDefinition(context, element, name, value).orNull();
            if (predefined != null) {
                return Result.fine(element, name, value, predefined.getColor());
            }
        }

        if (array.size() < 3) {
            throw this.fail(element, name, value, "RGB requires a list of 3 numbers");
        }

        int red = this.color(element, name, value, "Red", this.redParser.parseWithValue(context, element, value).orFail());
        int green = this.color(element, name, value, "Green", this.greenParser.parseWithValue(context, element, value).orFail());
        int blue = this.color(element, name, value, "Blue", this.blueParser.parseWithValue(context, element, value).orFail());
        return Result.fine(element, name, value, Color.fromRGB(red, green, blue));
    }

    private int color(Element element, String name, String value, String friendly, int color) throws ParserException {
        if (color < 0 || color > 255) {
            throw this.fail(element, name, value, friendly + " (" + color + ") must be in range of 0-255");
        }

        return color;
    }
}
