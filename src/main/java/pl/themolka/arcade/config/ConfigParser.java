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

package pl.themolka.arcade.config;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;

public abstract class ConfigParser<T extends IConfig> extends NodeParser<T>
                                                      implements InstallableParser {
    protected Parser<String> idParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.idParser = context.id();
    }

    protected String parseOptionalId(Node node) throws ParserException {
        return this.idParser.parse(this.id(node)).orDefaultNull();
    }

    protected String parseRequiredId(Node node) throws ParserException {
        return this.idParser.parse(this.id(node)).orFail();
    }

    private Property id(Node node) {
        return node.property("id");
    }
}
