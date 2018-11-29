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

import pl.themolka.arcade.dom.Document;
import pl.themolka.arcade.dom.Element;

import java.util.Set;

/**
 * Something that can or cannot parse T.
 * <b>The result cannot be {@code null}!</b>
 */
public interface Parser<T> {
    Set<Object> expect();

    default Result<T> parse(Document document) {
        return document != null && document.hasRoot() ? this.parse(document.getRoot())
                                                      : Result.empty();
    }

    default Result<T> parse(Element element) {
        return element != null ? this.parseWithDefinition(element, element.getName(), element.getValue())
                               : Result.empty();
    }

    default Result<T> parseWithDefinition(String name, String value) {
        return this.parseWithDefinition(null, name, value);
    }

    Result<T> parseWithDefinition(Element element, String name, String value);

    default Result<T> parseWithName(Element element, String name) {
        return this.parseWithDefinition(element, name, element != null ? element.getValue() : null);
    }

    default Result<T> parseWithValue(Element element, String value) {
        return element != null ? this.parseWithDefinition(element, element.getName(), value)
                               : Result.empty();
    }
}
