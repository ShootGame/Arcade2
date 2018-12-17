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

package pl.themolka.arcade.attribute;

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

@Produces(AttributeKey.class)
public class AttributeKeyParser extends ElementParser<AttributeKey>
                                implements InstallableParser {
    private Parser<String> namespaceParser;
    private Parser<String> keyParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.namespaceParser = library.text();
        this.keyParser = library.text();
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("attribute key, such as 'generic.attackDamage'");
    }

    @Override
    protected Result<AttributeKey> parseElement(Context context, Element element, String name, String value) throws ParserException {
        String[] input = value.split("\\.", 2);
        if (input.length != 2) {
            throw this.fail(element, name, value, "Requires a namespace and a key separated with a dot");
        }

        String namespace = this.normalizeInput(this.namespaceParser.parseWithDefinition(context, element, name, input[0]).orFail());
        String key = this.normalizeInput(this.keyParser.parseWithDefinition(context, element, name, input[1]).orFail());
        return Result.fine(element, name, value, new FixedAttributeKey(namespace, key));
    }
}
