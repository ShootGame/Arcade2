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

package pl.themolka.arcade.filter.operator;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.filter.BaseFilterParser;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserUtils;

import java.util.LinkedHashSet;
import java.util.Set;

public class BaseOperatorParser<T extends Operator.Config<?>> extends BaseFilterParser<T>
                                                              implements InstallableParser {
    private Parser<Filter.Config> filterParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.filterParser = context.type(Filter.Config.class);
    }

    @Override
    protected String expectType() {
        return "filter condition operator";
    }

    protected Set<Filter.Config<?>> parseBody(Node node, String name, String value) throws ParserException {
        Set<Filter.Config<?>> body = new LinkedHashSet<>();
        for (Node bodyNode : node.children()) {
            body.add(this.filterParser.parse(bodyNode).orFail());
        }

        if (ParserUtils.ensureNotEmpty(body)) {
            throw this.fail(node, name, value, "No body defined");
        }

        return body;
    }
}
