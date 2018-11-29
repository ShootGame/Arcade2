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

package pl.themolka.arcade.parser;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.dom.Property;

/**
 * Simple and easy {@link Property} parsing.
 */
public abstract class PropertyParser<T> extends ElementParser<T> {
    public PropertyParser() {
    }

    @Override
    protected Result<T> parseElement(Element element, String name, String value) throws ParserException {
        if (element instanceof Property) {
            return this.parseProperty((Property) element, name, value);
        }

        throw this.fail(element, name, value, "Not a property");
    }

    protected abstract Result<T> parseProperty(Property property, String name, String value) throws ParserException;
}
