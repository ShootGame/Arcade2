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

package pl.themolka.arcade.environment;

import pl.themolka.arcade.dom.DOMException;
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

@Produces(Environment.class)
public class EnvironmentParser extends NodeParser<Environment>
                               implements InstallableParser {
    private Parser<EnvironmentType> environmentTypeParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.environmentTypeParser = library.type(EnvironmentType.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("environment");
    }

    @Override
    protected Result<Environment> parseNode(Context context, Node node, String name, String value) throws ParserException {
        EnvironmentType type = this.environmentTypeParser.parse(context, node.property("type", "of")).orDefault(Environment.DEFAULT_TYPE);
        try {
            return Result.fine(node, name, value, type.buildEnvironment(node));
        } catch (DOMException ex) {
            throw this.fail(node, name, value, "Could not build environment", ex);
        }
    }
}
