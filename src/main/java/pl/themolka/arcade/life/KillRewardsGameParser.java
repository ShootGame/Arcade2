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

import java.util.ArrayList;
import java.util.List;

@Produces(KillRewardsGame.Config.class)
public class KillRewardsGameParser extends GameModuleParser<KillRewardsGame, KillRewardsGame.Config>
                                   implements InstallableParser {
    private Parser<KillReward.Config> killRewardParser;

    public KillRewardsGameParser() {
        super(KillRewardsGame.class);
    }

    @Override
    public Node define(Node source) {
        return source.firstChild("kill-rewards", "killrewards", "kill-reward", "killreward");
    }

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        super.install(library);
        this.killRewardParser = library.type(KillReward.Config.class);
    }

    @Override
    protected Result<KillRewardsGame.Config> parseTree(Context context, Node node, String name) throws ParserException {
        List<KillReward.Config> rewards = new ArrayList<>();
        for (Node rewardNode : node.children("reward")) {
            rewards.add(this.killRewardParser.parse(context, rewardNode).orFail());
        }

        if (ParserUtils.ensureNotEmpty(rewards)) {
            throw this.fail(node, name, null, "No rewards defined");
        }

        return Result.fine(node, name, new KillRewardsGame.Config() {
            public Ref<List<KillReward.Config>> rewards() { return Ref.ofProvided(rewards); }
        });
    }
}
