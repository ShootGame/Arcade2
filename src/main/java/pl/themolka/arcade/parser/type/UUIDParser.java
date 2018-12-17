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

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@Produces(UUID.class)
public class UUIDParser extends ElementParser<UUID> {
    @Override
    public Set<Object> expect() {
        return Collections.singleton("an UUID");
    }

    @Override
    protected Result<UUID> parseElement(Context context, Element element, String name, String value) throws ParserException {
        try {
            UUID result = this.parseUnknown(value);
            if (result != null) {
                return Result.fine(element, name, value, result);
            }

            throw this.fail(element, name, value, "Unknown UUID format");
        } catch (IllegalArgumentException ex) {
            throw this.fail(element, name, value, "Illegal UUID syntax", ex);
        }
    }

    protected UUID parseUnknown(String input) throws IllegalArgumentException {
        switch (input.length()) {
            case 32: return this.parseTrimmed(input);
            case 36: return this.parseStandard(input);
            default: return null;
        }
    }

    protected UUID parseTrimmed(String input) throws IllegalArgumentException {
        return UUID.fromString(input.substring(0, 8) + "-" +
                               input.substring(8, 12) + "-" +
                               input.substring(12, 16) + "-" +
                               input.substring(16, 20) + "-" +
                               input.substring(20, 32));
    }

    protected UUID parseStandard(String input) throws IllegalArgumentException {
        return UUID.fromString(input);
    }
}
