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

package pl.themolka.arcade.portal;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserUtils;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.LinkedHashSet;
import java.util.Set;

@Produces(PortalsGame.Config.class)
public class PortalsGameParser extends GameModuleParser<PortalsGame, PortalsGame.Config>
                               implements InstallableParser {
    private Parser<Portal.Config> portalParser;

    public PortalsGameParser() {
        super(PortalsGame.class);
    }

    @Override
    public Node define(Node source) {
        return source.firstChild("portals", "portal");
    }

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        super.install(library);
        this.portalParser = library.type(Portal.Config.class);
    }

    @Override
    protected Result<PortalsGame.Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
        Set<Portal.Config> portals = new LinkedHashSet<>();
        for (Node portalNode : node.children("portal")) {
            portals.add(this.portalParser.parse(context, portalNode).orFail());
        }

        if (ParserUtils.ensureNotEmpty(portals)) {
            throw this.fail(node, name, value, "No portals defined");
        }

        return Result.fine(node, name, value, new PortalsGame.Config() {
            public Ref<Set<Portal.Config>> portals() { return Ref.ofProvided(portals); }
        });
    }
}
