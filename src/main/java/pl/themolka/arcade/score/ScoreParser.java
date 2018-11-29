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

package pl.themolka.arcade.score;

import pl.themolka.arcade.config.ConfigParser;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.Set;

@Produces(Score.Config.class)
public class ScoreParser extends ConfigParser<Score.Config>
                         implements InstallableParser {
    private Parser<Double> deathLossParser;
    private Parser<Double> initialScoreParser;
    private Parser<Double> killRewardParser;
    private Parser<Double> limitParser;
    private Parser<String> nameParser;
    private Parser<Ref> ownerParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.deathLossParser = context.type(Double.class);
        this.initialScoreParser = context.type(Double.class);
        this.killRewardParser = context.type(Double.class);
        this.limitParser = context.type(Double.class);
        this.nameParser = context.type(String.class);
        this.ownerParser = context.type(Ref.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("score");
    }

    @Override
    protected Result<Score.Config> parseNode(Node node, String name, String value) throws ParserException {
        double deathLoss = this.deathLossParser.parse(node.property("death-loss", "deathloss", "death")).orDefault(Score.Config.DEFAULT_DEATH_LOSS);
        double initialScore = this.initialScoreParser.parse(node.property("initial-score", "initialscore")).orDefault(Score.Config.DEFAULT_INITIAL_SCORE);
        double killReward = this.killRewardParser.parse(node.property("kill-reward", "killreward", "kill")).orDefault(Score.Config.DEFAULT_KILL_REWARD);
        double limit = this.limitParser.parse(node.property("limit")).orDefault(Score.Config.DEFAULT_LIMIT);
        String scoreName = this.nameParser.parse(node.property("name")).orDefaultNull();
        Ref<Participator.Config<?>> owner = this.ownerParser.parse(node.property("owner")).orFail();

        return Result.fine(node, name, value, new Score.Config() {
            public Ref<Double> deathLoss() { return Ref.ofProvided(deathLoss); }
            public Ref<Double> initialScore() { return Ref.ofProvided(initialScore); }
            public Ref<Double> killReward() { return Ref.ofProvided(killReward); }
            public Ref<Double> limit() { return Ref.ofProvided(limit); }
            public Ref<String> name() { return Ref.ofProvided(scoreName); }
            public Ref<Participator.Config<?>> owner() { return owner; }
        });
    }
}
