package pl.themolka.arcade.damage;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

@Produces(RageGame.Config.class)
public class RageGameParser extends GameModuleParser<RageGame, RageGame.Config>
                            implements InstallableParser {
    private Parser<Double> damageParser;

    public RageGameParser() {
        super(RageGame.class);
    }

    @Override
    public Node define(Node source) {
        return source.firstChild("rage", "instant-kill", "instantkill");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.damageParser = context.type(Double.class);
    }

    @Override
    protected ParserResult<RageGame.Config> parseNode(Node node, String name, String value) throws ParserException {
        double damage = this.damageParser.parseWithDefinition(node, name, value).orDefault(RageGame.Config.DEFAULT_DAMAGE);
        if (damage <= 0) {
            throw this.fail(node, name, value, "Damage must be positive (greater than 0)");
        }

        return ParserResult.fine(node, name, value, new RageGame.Config() {
            public Ref<Double> damage() { return Ref.ofProvided(damage); }
        });
    }
}
