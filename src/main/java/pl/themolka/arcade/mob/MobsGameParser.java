package pl.themolka.arcade.mob;

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

@Produces(MobsGame.Config.class)
public class MobsGameParser extends GameModuleParser<MobsGame, MobsGame.Config>
                            implements InstallableParser {
    private Parser<MobSpawnRule.Config> ruleParser;
    private Parser<Boolean> denyNautralParser;

    public MobsGameParser() {
        super(MobsGame.class);
    }

    @Override
    public Node define(Node source) {
        return source.firstChild("mobs");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.ruleParser = context.type(MobSpawnRule.Config.class);
        this.denyNautralParser = context.type(Boolean.class);
    }

    @Override
    protected ParserResult<MobsGame.Config> parseNode(Node node, String name, String value) throws ParserException {
        List<MobSpawnRule.Config> rules = new ArrayList<>();
        for (Node ruleNode : node.children("rule")) {
            rules.add(this.ruleParser.parse(ruleNode).orFail());
        }

        boolean denyNatural = this.denyNautralParser.parse(node.property("deny-natural")).orDefault(false);

        if (!denyNatural && ParserUtils.ensureNotEmpty(rules)) {
            throw this.fail(node, name, value, "No rules defined");
        }

        return ParserResult.fine(node, name, value, new MobsGame.Config() {
            public List<MobSpawnRule.Config> rules() { return rules; }
            public boolean denyNatural() { return denyNatural; }
        });
    }
}
