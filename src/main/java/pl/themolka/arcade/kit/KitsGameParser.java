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

package pl.themolka.arcade.kit;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserUtils;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.LinkedHashSet;
import java.util.Set;

@Produces(KitsGame.Config.class)
public class KitsGameParser extends GameModuleParser<KitsGame, KitsGame.Config>
                            implements InstallableParser {
    private Parser<Kit.Config> kitParser;

    public KitsGameParser() {
        super(KitsGame.class);
    }

    @Override
    public Node define(Node source) {
        return source.firstChild("kits", "kit");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.kitParser = context.type(Kit.Config.class);
    }

    @Override
    protected Result<KitsGame.Config> parseTree(Node node, String name) throws ParserException {
        Set<Kit.Config> kits = new LinkedHashSet<>();
        for (Node kitNode : node.children("kit", "set", "package")) {
            kits.add(this.kitParser.parse(kitNode).orFail());
        }

        if (ParserUtils.ensureNotEmpty(kits)) {
            throw this.fail(node, name, null, "No kits defined");
        }

        return Result.fine(node, name, new KitsGame.Config() {
            public Ref<Set<Kit.Config>> kits() { return Ref.ofProvided(kits); }
        });
    }
}
