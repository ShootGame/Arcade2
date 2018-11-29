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

package pl.themolka.arcade.filter.matcher;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.team.Team;
import pl.themolka.arcade.team.TeamHolder;

public class TeamMatcher extends ConfigurableMatcher<Team> {
    protected TeamMatcher(Config config) {
        super(config.value().get());
    }

    @Override
    public boolean find(Object object) {
        if (object instanceof Team) {
            return this.matches((Team) object);
        } else if (object instanceof TeamHolder) {
            return this.matches((TeamHolder) object);
        }

        return false;
    }

    public boolean matches(TeamHolder teamHolder) {
        return teamHolder != null && this.matches(teamHolder.getTeam());
    }

    @NestedParserName("team")
    @Produces(Config.class)
    public static class MatcherParser extends BaseMatcherParser<Config>
                                      implements InstallableParser {
        private Parser<Ref> teamParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.teamParser = context.type(Ref.class);
        }

        @Override
        protected Result<Config> parseNode(Node node, String name, String value) throws ParserException {
            Ref<Team> team = this.teamParser.parseWithDefinition(node, name, value).orFail();

            return Result.fine(node, name, value, new Config() {
                public Ref<Team> value() { return team; }
            });
        }
    }

    public interface Config extends ConfigurableMatcher.Config<TeamMatcher, Team> {
        @Override
        default TeamMatcher create(Game game, Library library) {
            return new TeamMatcher(this);
        }
    }
}
