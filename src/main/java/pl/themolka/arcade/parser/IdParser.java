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

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Property;

import java.util.Collections;
import java.util.Set;

@Produces(String.class)
@Silent
public class IdParser extends PropertyParser<String> {
    @Override
    public Set<Object> expect() {
        return Collections.singleton("unique ID matching " + Ref.ID_PATTERN.pattern());
    }

    @Override
    protected Result<String> parseProperty(Property property, String name, String value) throws ParserException {
        if (!this.validId(value)) {
            throw this.fail(property, name, value, "Invalid ID syntax");
        }

        return Result.fine(property, name, value, value);
    }

    private boolean validId(String id) throws ParserException {
        return Ref.ID_PATTERN.matcher(id).matches();
    }
}
