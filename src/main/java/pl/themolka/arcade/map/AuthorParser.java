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
import java.util.UUID;

@Produces(Author.class)
public class AuthorParser extends NodeParser<Author>
                          implements InstallableParser {
    private Parser<String> usernameParser;
    private Parser<UUID> uuidParser;
    private Parser<String> descriptionParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.usernameParser = library.text();
        this.uuidParser = library.type(UUID.class);
        this.descriptionParser = library.text();
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("an author");
    }

    @Override
    protected Result<Author> parsePrimitive(Context context, Node node, String name, String value) throws ParserException {
        UUID uuid = this.uuidParser.parse(context, node.property("uuid", "uid")).orDefaultNull();
        String username = this.usernameParser.parseWithDefinition(context, node, name, value).orFail(); // required
        String description = this.descriptionParser.parse(context, node.property("description", "contribution")).orDefaultNull();
        return Result.fine(node, name, value, Author.of(uuid, username, description));
    }
}
