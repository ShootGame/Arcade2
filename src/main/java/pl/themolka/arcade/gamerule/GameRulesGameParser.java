package pl.themolka.arcade.gamerule;

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

import java.util.LinkedHashSet;
import java.util.Set;

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
        super.install(context);
        this.ruleParser = context.type(GameRule.class);
    }

    @Override
    protected Result<GameRulesGame.Config> parseTree(Node node, String name) throws ParserException {
        Set<GameRule> rules = new LinkedHashSet<>();
        for (Node ruleNode : node.children()) {
            GameRule rule = this.ruleParser.parse(ruleNode).orFail();
            for (GameRule existingRule : rules) {
                if (existingRule.getKey().equals(rule.getKey())) {
                    throw this.fail(ruleNode, "Rule '" + rule.getKey() + "' is already defined");
                }
            }

            rules.add(rule);
        }

        if (ParserUtils.ensureNotEmpty(rules)) {
            throw this.fail(node, name, null, "No rules defined");
        }

        return Result.fine(node, name, new GameRulesGame.Config() {
            public Ref<Set<GameRule>> rules() { return Ref.ofProvided(rules); }
        });
    }
}
