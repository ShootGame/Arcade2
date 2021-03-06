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

import org.apache.commons.lang3.StringUtils;
import pl.themolka.arcade.dom.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Simple and easy {@link Enum} parsing.
 */
public class EnumParser<T extends Enum<T>> extends ElementParser<T> {
    private final Class<T> type;

    public EnumParser(Class<T> type) {
        this.type = Objects.requireNonNull(type, "type cannot be null");
    }

    @Override
    public Set<Object> expect() {
        return Stream.of(this.type.getEnumConstants())
                .map(constant -> toEnumValue(constant.name()))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Object> expectCompact() {
        return Collections.singleton(this.type.getSimpleName() + " constant");
    }

    @Override
    protected Result<T> parseElement(Context context, Element element, String name, String value) throws ParserException {
        T result = parse(this.type, value);
        if (result != null) {
            return Result.fine(element, name, value, result);
        }

        throw this.fail(element, name, value, "Unknown " + this.type.getSimpleName() + " property");
    }

    public Class<T> getType() {
        return this.type;
    }

    public static String toEnumValue(String input) {
        return input.toUpperCase().replace(" ", "_")
                                  .replace("-", "_");
    }

    public static String toPrettyValue(String input) {
        return input.toLowerCase().replace("_", " ");
    }

    public static String toPrettyCapitalized(String input) {
        String[] words = toPrettyValue(input).split(" ");
        for (int i = 0; i < words.length; i++) {
            words[i] = words[0].length() == 1 ? words[0] : StringUtils.capitalize(words[0]);
        }

        return StringUtils.join(words, ' ');
    }

    public <E extends Enum<E>> List<Object> valueNames(Class<E> enumClass) {
        List<Object> valueNames = new ArrayList<>();
        for (E constant : enumClass.getEnumConstants()) {
            valueNames.add(toPrettyValue(constant.name()));
        }

        return valueNames;
    }

    public static <E extends Enum<E>> E parse(Class<E> type, String input) {
        input = toEnumValue(input);

        try {
            // try the fastest way first
            return Enum.valueOf(type, input);
        } catch (IllegalArgumentException ex) {
            // try case-insensitive
            for (E constant : type.getEnumConstants()) {
                if (constant.name().equalsIgnoreCase(input)) {
                    return constant;
                }
            }

            // try contains
            for (E constant : type.getEnumConstants()) {
                if (constant.name().toUpperCase().contains(input)) {
                    return constant;
                }
            }
        }

        return null;
    }
}
