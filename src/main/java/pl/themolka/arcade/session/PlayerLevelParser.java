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

package pl.themolka.arcade.session;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.Set;

@Produces(PlayerLevel.class)
public class PlayerLevelParser extends ElementParser<PlayerLevel>
                               implements InstallableParser {
    private Parser<Integer> levelParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.levelParser = context.type(Integer.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("experience level");
    }

    @Override
    protected Result<PlayerLevel> parseElement(Element element, String name, String value) throws ParserException {
        int level = this.levelParser.parseWithDefinition(element, name, value).orFail();
        if (level < 0) {
            throw this.fail(element, name, value, "Level cannot be negative (smaller than 0)");
        }

        return Result.fine(element, name, value, new PlayerLevel(level));
    }
}
