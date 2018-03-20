package pl.themolka.arcade.gamerule;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.ParserUtils;
import pl.themolka.arcade.parser.Produces;

import java.util.ArrayList;
import java.util.List;

@Produces(GameRulesGame.Config.class)
public class GameRulesGameParser extends GameModuleParser<GameRulesGame, GameRulesGame.Config>
                                 implements InstallableParser {
    private Parser<GameRule> ruleParser;

    public GameRulesGameParser() {
        super(GameRulesGame.class);
    }

    @Override
    public Node define(Node source) {
        return source.firstChild("gamerules", "game-rules", "gamerule", "game-rule");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.ruleParser = context.type(GameRule.class);
    }

    @Override
    protected ParserResult<GameRulesGame.Config> parseTree(Node node, String name) throws ParserException {
        List<GameRule> rules = new ArrayList<>();
        for (Node ruleNode : node.children()) {
            rules.add(this.ruleParser.parse(ruleNode).orFail());
        }

        if (ParserUtils.ensureNotEmpty(rules)) {
            throw this.fail(node, name, null, "No rules defined");
        }

        return ParserResult.fine(node, name, new GameRulesGame.Config() {
            public List<GameRule> rules() { return rules; }
        });
    }
}
