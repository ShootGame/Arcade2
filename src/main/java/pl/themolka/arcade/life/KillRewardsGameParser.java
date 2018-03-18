package pl.themolka.arcade.life;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.ArrayList;
import java.util.List;

@Produces(KillRewardsGame.Config.class)
public class KillRewardsGameParser extends GameModuleParser<KillRewardsGame, KillRewardsGame.Config>
                                   implements InstallableParser {
    private Parser<KillReward> killRewardParser;

    public KillRewardsGameParser() {
        super(KillRewardsGame.class);
    }

    @Override
    public Node define(Node source) {
        return source.firstChild("kill-rewards", "killrewards", "kill-reward", "killreward");
    }

    @Override
    public void install(ParserContext context) {
        this.killRewardParser = context.type(KillReward.class);
    }

    @Override
    protected ParserResult<KillRewardsGame.Config> parseTree(Node node, String name) throws ParserException {
        List<KillReward> rewards = new ArrayList<>();
        for (Node rewardNode : node.children("reward")) {
            rewards.add(this.killRewardParser.parse(rewardNode).orFail());
        }

        return ParserResult.fine(node, name, new KillRewardsGame.Config() {
            public List<KillReward> rewards() { return rewards; }
        });
    }
}
