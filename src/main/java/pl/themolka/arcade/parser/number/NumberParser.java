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

package pl.themolka.arcade.parser.number;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.Set;

public abstract class NumberParser<T extends Number> extends ElementParser<T> {
    public static final String INFINITY = "oo";

    public NumberParser() {
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("a number");
    }

    @Override
    protected Result<T> parseElement(Context context, Element element, String name, String value) throws ParserException {
        if (isPositiveInfinity(value)) {
            return Result.fine(element, name, value, this.positiveInfinity());
        } else if (isNegativeInfinity(value)) {
            return Result.fine(element, name, value, this.negativeInfinity());
        }

        try {
            return Result.fine(element, name, value, this.parse(value));
        } catch (NumberFormatException cause) {
            throw this.fail(element, name, value, "Illegal number (or not a number)", cause);
        }
    }

    protected abstract T parse(String input) throws NumberFormatException;

    protected abstract T positiveInfinity();

    protected abstract T negativeInfinity();

    static boolean isPositiveInfinity(String input) {
        return input != null && (input.equals("+" + INFINITY) || input.equals(INFINITY)); // oo is positive by default
    }

    static boolean isNegativeInfinity(String input) {
        return input != null && input.equals("-" + INFINITY);
    }
}
