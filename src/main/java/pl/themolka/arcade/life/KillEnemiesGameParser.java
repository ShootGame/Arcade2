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

package pl.themolka.arcade.life;

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

import java.util.HashSet;
import java.util.Set;

@Produces(KillEnemiesGame.Config.class)
public class KillEnemiesGameParser extends GameModuleParser<KillEnemiesGame, KillEnemiesGame.Config>
                                   implements InstallableParser {
    private Parser<KillEnemies.Config> objectiveParser;

    public KillEnemiesGameParser() {
        super(KillEnemiesGame.class);
    }

    @Override
    public Node define(Node source) {
        return source.firstChild("kill-enemies", "killenemies", "kill-enemy", "killenemy", "kill-all", "killall");
    }

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        super.install(library);
        this.objectiveParser = library.type(KillEnemies.Config.class);
    }

    @Override
    protected Result<KillEnemiesGame.Config> parseTree(Context context, Node node, String name) throws ParserException {
        Set<KillEnemies.Config> objectives = new HashSet<>();
        for (Node objectiveNode : node.children("participator", "competitor", "team")) {
            objectives.add(this.objectiveParser.parse(context, objectiveNode).orFail());
        }

        if (ParserUtils.ensureNotEmpty(objectives)) {
            throw this.fail(node, name, null, "No objectives defined");
        }

        return Result.fine(node, name, new KillEnemiesGame.Config() {
            public Ref<Set<KillEnemies.Config>> objectives() { return Ref.ofProvided(objectives); }
        });
    }
}
