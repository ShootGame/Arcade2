package pl.themolka.arcade.mob;

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
    protected Result<MobsGame.Config> parseNode(Node node, String name, String value) throws ParserException {
        boolean denyNatural = this.denyNautralParser.parse(node.property("deny-natural")).orDefault(false);

        List<MobSpawnRule.Config> rules = new ArrayList<>();
        if (!denyNatural) {
            for (Node ruleNode : node.children("rule")) {
                rules.add(this.ruleParser.parse(ruleNode).orFail());
            }

            if (ParserUtils.ensureNotEmpty(rules)) {
                throw this.fail(node, name, value, "No rules defined");
            }
        }

        return Result.fine(node, name, value, new MobsGame.Config() {
            public Ref<List<MobSpawnRule.Config>> rules() { return Ref.ofProvided(rules); }
            public Ref<Boolean> denyNatural() { return Ref.ofProvided(denyNatural); }
        });
    }
}
