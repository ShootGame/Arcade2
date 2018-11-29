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

package pl.themolka.arcade.map;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.Set;

@Produces(MapTime.class)
public class MapTimeParser extends NodeParser<MapTime>
                           implements InstallableParser {
    public static final boolean DEFAULT_IS_LOCKED = false;
    public static final long DEFAULT_TIME_TICKS = MapTime.defaultTime().getTicks();

    private Parser<Boolean> booleanParser;
    private Parser<MapTimeConstants> contantParser;
    private Parser<Long> longParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.booleanParser = context.type(Boolean.class);
        this.contantParser = context.type(MapTimeConstants.class);
        this.longParser = context.type(Long.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("a map time");
    }

    @Override
    protected Result<MapTime> parseNode(Node node, String name, String value) throws ParserException {
        MapTime result = MapTime.ofTicks(this.parseTicks(node, name, value));
        result.setLocked(this.booleanParser.parse(node.property("locked", "lock")).orDefault(DEFAULT_IS_LOCKED));
        return Result.fine(node, name, value, result);
    }

    private long parseTicks(Element element, String name, String value) throws ParserException {
        MapTimeConstants constant = this.contantParser.parseWithDefinition(element, name, value).orNull();
        if (constant != null) {
            return constant.ticks();
        }

        return this.longParser.parseWithDefinition(element, name, value).orDefault(DEFAULT_TIME_TICKS);
    }
}
