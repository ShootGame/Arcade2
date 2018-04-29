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
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

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
    protected ParserResult<Score.Config> parseNode(Node node, String name, String value) throws ParserException {
        double deathLoss = this.deathLossParser.parse(node.property("death-loss", "deathloss", "death")).orDefault(Score.Config.DEFAULT_DEATH_LOSS);
        double initialScore = this.initialScoreParser.parse(node.property("initial-score", "initialscore")).orDefault(Score.Config.DEFAULT_INITIAL_SCORE);
        double killReward = this.killRewardParser.parse(node.property("kill-reward", "killreward", "kill")).orDefault(Score.Config.DEFAULT_KILL_REWARD);
        double limit = this.limitParser.parse(node.property("limit")).orDefault(Score.Config.DEFAULT_LIMIT);
        String scoreName = this.nameParser.parse(node.property("name")).orDefaultNull();
        Ref<Participator.Config<?>> owner = this.ownerParser.parse(node.property("owner")).orFail();

        return ParserResult.fine(node, name, value, new Score.Config() {
            public double deathLoss() { return deathLoss; }
            public double initialScore() { return initialScore; }
            public double killReward() { return killReward; }
            public double limit() { return limit; }
            public String name() { return scoreName; }
            public Ref<Participator.Config<?>> owner() { return owner; }
        });
    }
}