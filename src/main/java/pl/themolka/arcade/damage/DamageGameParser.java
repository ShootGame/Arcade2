package pl.themolka.arcade.damage;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.LinkedHashSet;
import java.util.Set;

@Produces(DamageGame.Config.class)
public class DamageGameParser extends GameModuleParser<DamageGame, DamageGame.Config>
                              implements InstallableParser {
    private Parser<DamageRule.Config> ruleParser;

    public DamageGameParser() {
        super(DamageGame.class);
    }

    @Override
    public Node define(Node source) {
        return source.firstChild("damage-rules", "damagerules");
    }

    @Override
    public void install(ParserContext context) {
        this.ruleParser = context.type(DamageRule.Config.class);
    }

    @Override
    protected ParserResult<DamageGame.Config> parseTree(Node node, String name) throws ParserException {
        Set<DamageRule.Config> rules = new LinkedHashSet<>();
        for (Node ruleNode : node.children("rule")) {
            rules.add(this.ruleParser.parse(ruleNode).orFail());
        }

        if (rules.isEmpty()) {
            throw this.fail(node, name, null, "No rules defined");
        }

        return ParserResult.fine(node, name, new DamageGame.Config() {
            public Set<DamageRule.Config> rules() { return rules; }
        });
    }
}
