package pl.themolka.arcade.life;

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
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.killRewardParser = context.type(KillReward.Config.class);
    }

    @Override
    protected Result<KillRewardsGame.Config> parseTree(Node node, String name) throws ParserException {
        List<KillReward.Config> rewards = new ArrayList<>();
        for (Node rewardNode : node.children("reward")) {
            rewards.add(this.killRewardParser.parse(rewardNode).orFail());
        }

        if (ParserUtils.ensureNotEmpty(rewards)) {
            throw this.fail(node, name, null, "No rewards defined");
        }

        return Result.fine(node, name, new KillRewardsGame.Config() {
            public Ref<List<KillReward.Config>> rewards() { return Ref.ofProvided(rewards); }
        });
    }
}
