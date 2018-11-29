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

import java.util.LinkedHashMap;

/**
 * A map holding nested parsers by their name given in the {@link NestedParserName}.
 */
public class NestedParserMap<T extends Parser<?>> extends LinkedHashMap<String, T> {
    private final ParserContext context;

    public NestedParserMap(ParserContext context) {
        this.context = context;
    }

    public T parse(String name) {
        return this.get(name);
    }

    public void scan(Class<?> clazz) throws ParserNotSupportedException {
        for (Class<?> nested : clazz.getDeclaredClasses()) {
            this.scanDedicatedClass(nested);
        }
    }

    public void scanDedicatedClass(Class<?> clazz) throws ParserNotSupportedException {
        NestedParserName nameProvider = clazz.getDeclaredAnnotation(NestedParserName.class);
        if (nameProvider == null || !Parser.class.isAssignableFrom(clazz)) {
            return;
        }

        String[] names = nameProvider.value();
        if (names != null) {
            T parser = this.context.of((Class<T>) clazz);
            if (parser instanceof InstallableParser) {
                ((InstallableParser) parser).install(this.context);
            }

            for (String name : names) {
                this.put(name, parser);
            }
        }
    }
}
