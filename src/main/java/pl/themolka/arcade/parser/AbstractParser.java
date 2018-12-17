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
import pl.themolka.arcade.dom.EmptyElement;

import java.util.Objects;
import java.util.Set;

/**
 * Base class for all parsers with simple exception handling.
 *
 * <ul>
 *   <li>The default behavior of parsers is {@link TextParser}.</li>
 *   <li>{@link Enum}s can easily be parsed using the {@link EnumParser}.</li>
 * </ul>
 *
 * Normally, this class is used as a base class for all DOM type parsers. All
 * parsers should inherit respective DOM type parser. These are as follows:
 *
 * <ul>
 *   <li><b>{@link ElementParser}</b> for non-null key/value pair which can
 *   parse all {@link Element}s.</li>
 *   <li><b>{@link NodeParser}</b> for primitive or tree type
 *   {@link pl.themolka.arcade.dom.Node}s.</li>
 *   <li><b>{@link PropertyParser}</b> for non-null
 *   {@link pl.themolka.arcade.dom.Property}</li>
 * </ul>
 */
public abstract class AbstractParser<T> extends ParserValidation
                                        implements Parser<T> {
    public AbstractParser() {
    }

    @Override
    public Result<T> parseWithDefinition(Context context, Element element, String name, String value) {
        Objects.requireNonNull(context, "context cannot be null");

        if (element == null) {
            element = EmptyElement.empty();
        } else if (name == null) {
            name = element.getName();
        }

        if (value == null) {
            value = element.getValue();
        }

        try {
            String normalizedName = this.normalizeName(name);
            String normalizedValue = this.normalizeValue(value);

            if (normalizedName != null) {
                Result<T> result = this.parse(context, element, normalizedName, normalizedValue);
                if (result != null) {
                    return result;
                }

                throw new NullPointerException(this.getClass().getSimpleName() + " parser returned null");
            }
        } catch (ParserException cause) {
            return Result.fail(cause, name, value);
        }

        return Result.empty(element, name);
    }

    protected String normalizeInput(String input) {
        if (input != null) {
            input = input.trim();
            if (!input.isEmpty()) {
                return input;
            }
        }

        return null;
    }

    //
    // Abstract Methods
    //

    @Override
    public abstract Set<Object> expect();

    protected abstract Result<T> parse(Context context, Element element, String name, String value) throws ParserException;

    protected String normalizeName(String name) throws ParserException {
        return this.normalizeInput(name);
    }

    protected String normalizeValue(String value) throws ParserException {
        return this.normalizeInput(value);
    }
}
