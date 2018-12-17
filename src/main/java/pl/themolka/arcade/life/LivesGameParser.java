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

import org.bukkit.Sound;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.team.Team;

@Produces(LivesGame.Config.class)
public class LivesGameParser extends GameModuleParser<LivesGame, LivesGame.Config>
                             implements InstallableParser {
    private Parser<Integer> livesParser;
    private Parser<Ref> fallbackParser;
    private Parser<Boolean> announceParser;
    private Parser<Sound> soundParser;

    public LivesGameParser() {
        super(LivesGame.class);
    }

    @Override
    public Node define(Node source) {
        return source.firstChild("lives", "life");
    }

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        super.install(library);
        this.livesParser = library.type(Integer.class);
        this.fallbackParser = library.type(Ref.class);
        this.announceParser = library.type(Boolean.class);
        this.soundParser = library.type(Sound.class);
    }

    @Override
    protected Result<LivesGame.Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
        Result<Integer> livesResult = this.livesParser.parse(context, node);
        int lives = node.getName().equals("life") ? livesResult.orDefault(1) : livesResult.orFail();
        // ^ 1 is the default if the node name is singular form

        Ref<Team.Config> fallbackTeam = this.fallbackParser.parse(context, node.property("fallback", "return")).orDefault(Ref.empty());
        boolean announce = this.announceParser.parse(context, node.property("announce", "message")).orDefault(true);
        Sound sound = this.soundParser.parse(context, node.property("sound")).orDefault(LivesGame.Config.DEFAULT_SOUND);

        return Result.fine(node, name, value, new LivesGame.Config() {
            public Ref<Integer> lives() { return Ref.ofProvided(lives); }
            public Ref<Team.Config> fallbackTeam() { return fallbackTeam; }
            public Ref<Boolean> announce() { return Ref.ofProvided(announce); }
            public Ref<Sound> sound() { return Ref.ofProvided(sound); }
        });
    }
}
