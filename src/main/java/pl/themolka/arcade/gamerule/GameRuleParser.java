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

package pl.themolka.arcade.gamerule;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.Set;

@Produces(GameRule.class)
public class GameRuleParser extends NodeParser<GameRule>
                            implements InstallableParser {
    private Parser<GameRuleType> typeParser;
    private Parser<String> valueParser;
    private Parser<String> keyParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.typeParser = library.type(GameRuleType.class);
        this.valueParser = library.text();
        this.keyParser = library.text();
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("game rule");
    }

    @Override
    protected Result<GameRule> parsePrimitive(Context context, Node node, String name, String value) throws ParserException {
        GameRuleType type = this.typeParser.parseWithDefinition(context, node, name, name).orNull(); // name is the value
        String ruleValue = this.valueParser.parseWithDefinition(context, node, name, value).orFail();

        if (type != null) {
            return Result.fine(node, name, value, type.create(ruleValue));
        }

        String ruleKey = this.keyParser.parseWithValue(context, node, name).orFail();
        return Result.fine(node, name, value, new GameRule(ruleKey, ruleValue));
    }
}
