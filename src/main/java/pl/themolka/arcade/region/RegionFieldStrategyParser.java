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

package pl.themolka.arcade.region;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.EnumParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Set;

@Produces(IRegionFieldStrategy.class)
public class RegionFieldStrategyParser extends ElementParser<IRegionFieldStrategy>
                                       implements InstallableParser {
    private Parser<String> textParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.textParser = library.text();
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("region field strategy");
    }

    @Override
    protected Result<IRegionFieldStrategy> parseElement(Context context, Element element, String name, String value) throws ParserException {
        String text = this.textParser.parseWithDefinition(context, element, name, value).orFail();

        try {
            Field field = RegionFieldStrategy.class.getField(this.normalizeFieldName(text));

            if (field.isAccessible() && Modifier.isStatic(field.getModifiers())) {
                if (RegionFieldStrategy.class.isAssignableFrom(field.getType())) {
                    return Result.fine(element, name, value, (RegionFieldStrategy) field.get(null));
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }

        throw this.fail(element, name, value, "Unknown region field strategy type");
    }

    protected String normalizeFieldName(String input) {
        return EnumParser.toEnumValue(input);
    }
}
