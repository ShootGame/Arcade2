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

package pl.themolka.arcade.match;

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
import pl.themolka.arcade.time.Time;

@Produces(MatchGame.Config.class)
public class MatchGameParser extends GameModuleParser<MatchGame, MatchGame.Config>
                             implements InstallableParser {
    private Parser<Boolean> autoCycleParser;
    private Parser<Boolean> autoStartParser;
    private Parser<Time> startCountdownParser;
    private Parser<Observers.Config> observersParser;

    public MatchGameParser() {
        super(MatchGame.class);
    }

    @Override
    public Node define(Node source) {
        return source.firstChild("match");
    }

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        super.install(library);
        this.autoCycleParser = library.type(Boolean.class);
        this.autoStartParser = library.type(Boolean.class);
        this.startCountdownParser = library.type(Time.class);
        this.observersParser = library.type(Observers.Config.class);
    }

    @Override
    protected Result<MatchGame.Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
        boolean autoCycle = this.autoCycleParser.parse(context, node.property("auto-cycle", "autocycle")).orDefault(MatchGame.Config.DEFAULT_IS_AUTO_CYCLE);
        boolean autoStart = this.autoStartParser.parse(context, node.property("auto-start", "autostart")).orDefault(MatchGame.Config.DEFAULT_IS_AUTO_START);
        Time startCountdown = this.startCountdownParser.parse(context, node.property("start-countdown", "startcountdown")).orDefault(MatchGame.Config.DEFAULT_START_COUNTDOWN);
        Observers.Config observers = this.observersParser.parse(context, node.firstChild("observers")).orFail();

        return Result.fine(node, name, value, new MatchGame.Config() {
            public Ref<Boolean> autoCycle() { return Ref.ofProvided(autoCycle); }
            public Ref<Boolean> autoStart() { return Ref.ofProvided(autoStart); }
            public Ref<Time> startCountdown() { return Ref.ofProvided(startCountdown); }
            public Ref<Observers.Config> observers() { return Ref.ofProvided(observers); }
        });
    }
}
