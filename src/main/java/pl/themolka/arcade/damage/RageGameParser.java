package pl.themolka.arcade.damage;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.filter.Filter;
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
    private Parser<Ref> filterParser;

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
        this.filterParser = context.type(Ref.class);
    }

    @Override
    protected ParserResult<RageGame.Config> parseNode(Node node, String name, String value) throws ParserException {
        Ref<Filter.Config<?>> filter = this.filterParser.parse(node.property("filter")).orDefault(Ref.empty());

        return ParserResult.fine(node, name, value, new RageGame.Config() {
            public Ref<Filter.Config<?>> filter() { return filter; }
        });
    }
}
