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

package pl.themolka.arcade.spawn;

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
import java.util.Collections;
import java.util.Set;

@Produces(DirectionTranslator.class)
public class DirectionTranslatorParser extends ElementParser<DirectionTranslator>
                                       implements InstallableParser {
    private Parser<String> textParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.textParser = library.text();
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("direction mode");
    }

    @Override
    protected Result<DirectionTranslator> parseElement(Context context, Element element, String name, String value) throws ParserException {
        String text = this.textParser.parseWithDefinition(context, element, name, value).orFail();

        try {
            Field field = DirectionTranslator.class.getField(this.normalizeFieldName(text));
            field.setAccessible(true);

            if (DirectionTranslator.class.isAssignableFrom(field.getType())) {
                return Result.fine(element, name, value, (DirectionTranslator) field.get(null));
            }
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }

        throw this.fail(element, name, value, "Unknown direction mode");
    }

    protected String normalizeFieldName(String input) {
        return EnumParser.toEnumValue(input);
    }
}
