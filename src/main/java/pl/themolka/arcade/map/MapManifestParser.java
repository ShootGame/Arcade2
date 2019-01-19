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
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.game.IGameModuleConfig;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.util.Nulls;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@Produces(MapManifest.class)
public class MapManifestParser extends NodeParser<MapManifest>
                               implements InstallableParser {
    private Parser<WorldInfo> worldInfoParser;

    private Set<GameModuleParser<?, ?>> moduleParsers;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.worldInfoParser = library.type(WorldInfo.class);

        this.moduleParsers = new LinkedHashSet<>();
        for (Parser<?> parser : library.getContainer().getParsers()) {
            if (parser instanceof GameModuleParser) {
                this.moduleParsers.add((GameModuleParser<?, ?>) parser);
            }
        }
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("full map manifest");
    }

    @Override
    protected Result<MapManifest> parseTree(Context context, Node node, String name) throws ParserException {
        WorldInfo world = this.worldInfoParser.parse(context, node.child("world")).orDefaultNull();

        Set<IGameModuleConfig<?>> modules = new LinkedHashSet<>();
        Node modulesNode = this.getModulesNode(node);
        for (GameModuleParser<?, ?> parser : this.moduleParsers) {
            Node definedNode = parser.define(modulesNode);

            if (definedNode != null) {
                modules.add(parser.parse(context, definedNode).orFail());
            }
        }

        return Result.fine(node, name, new MapManifest(modules, world));
    }

    protected Node getModulesNode(Node root) throws ParserException {
        return Nulls.defaults(this.findJustOne(root, "modules", "module", "components", "component"), root);
    }

    private Node findJustOne(Node parent, String... names) throws ParserException {
        Node result = null;
        for (String name : names) {
            Node child = parent.firstChild(name);
            if (child == null) {
                continue;
            }

            if (result != null) {
                throw this.fail(child, "Modules node is already defined");
            }
            result = child;
        }

        return result;
    }
}
