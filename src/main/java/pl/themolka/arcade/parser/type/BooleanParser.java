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

import com.google.common.collect.ImmutableMap;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Produces(Boolean.class)
public class BooleanParser extends ElementParser<Boolean> {
    private static final Map<String, Boolean> values = ImmutableMap.<String, Boolean>builder()
            .putAll(Types.TRUE.toMap())
            .putAll(Types.FALSE.toMap())
            .build();

    @Override
    public Set<Object> expect() {
        return Collections.singleton("a boolean");
    }

    @Override
    protected Result<Boolean> parseElement(Element element, String name, String value) throws ParserException {
        Boolean result = values.get(value.toLowerCase());
        if (result != null) {
            return Result.fine(element, name, value, result);
        }

        throw this.fail(element, name, value, "Unknown boolean property");
    }

    enum Types {
        TRUE(true, "true", "1", "+", "yes", "allow", "on", "enable", "enabled"),
        FALSE(false, "false", "0", "-", "no", "deny", "off", "disable", "disabled");

        final boolean asBoolean;
        final String[] values;

        Types(boolean asBoolean, String... values) {
            this.asBoolean = asBoolean;
            this.values = values;
        }

        Map<String, Boolean> toMap() {
            Map<String, Boolean> collect = new HashMap<>();
            for (String value : this.values) {
                collect.put(value, this.asBoolean);
            }

            return collect;
        }
    }
}
