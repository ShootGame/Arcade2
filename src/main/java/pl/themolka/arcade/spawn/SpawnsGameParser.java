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

package pl.themolka.arcade.spawn;

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

import java.util.ArrayList;
import java.util.List;

@Produces(SpawnsGame.Config.class)
public class SpawnsGameParser extends GameModuleParser<SpawnsGame, SpawnsGame.Config>
                              implements InstallableParser {
    private Parser<Spawn.Config> spawnParser;

    public SpawnsGameParser() {
        super(SpawnsGame.class);
    }

    @Override
    public Node define(Node source) {
        return source.firstChild("spawns", "spawn");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.spawnParser = context.type(Spawn.Config.class);
    }

    @Override
    protected Result<SpawnsGame.Config> parseNode(Node node, String name, String value) throws ParserException {
        List<Spawn.Config<?>> spawns = new ArrayList<>();
        for (Node spawnNode : node.children()) {
            spawns.add(this.spawnParser.parse(spawnNode).orFail());
        }

        if (ParserUtils.ensureNotEmpty(spawns)) {
            throw this.fail(node, name, value, "No spawns defined");
        }

        return Result.fine(node, name, value, new SpawnsGame.Config() {
            public Ref<List<Spawn.Config<?>>> spawns() { return Ref.ofProvided(spawns); }
        });
    }
}
