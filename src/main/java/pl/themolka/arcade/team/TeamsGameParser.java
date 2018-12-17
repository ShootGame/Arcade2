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

package pl.themolka.arcade.team;

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

@Produces(TeamsGame.Config.class)
public class TeamsGameParser extends GameModuleParser<TeamsGame, TeamsGame.Config>
                             implements InstallableParser {
    private Parser<Team.Config> teamParser;

    public TeamsGameParser() {
        super(TeamsGame.class);
    }

    @Override
    public Node define(Node source) {
        return source.firstChild("teams", "team");
    }

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.teamParser = library.type(Team.Config.class);
    }

    @Override
    protected Result<TeamsGame.Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
        Set<Team.Config> teams = new LinkedHashSet<>();
        for (Node teamNode : node.children("team")) {
            teams.add(this.teamParser.parse(context, teamNode).orFail());
        }

        if (ParserUtils.ensureNotEmpty(teams)) {
            throw this.fail(node, name, value, "No teams defined");
        } else if (teams.size() > TeamsGame.Config.TEAMS_LIMIT) {
            throw this.fail(node, name, value, "Too many teams, reached limit of " + TeamsGame.Config.TEAMS_LIMIT + " teams");
        }

        return Result.fine(node, name, value, new TeamsGame.Config() {
            public Ref<Set<Team.Config>> teams() { return Ref.ofProvided(teams); }
        });
    }
}
