package pl.themolka.arcade.hunger;

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

@Produces(HungerGame.Config.class)
public class HungerGameParser extends GameModuleParser<HungerGame, HungerGame.Config>
                              implements InstallableParser {
    private Parser<Ref> filterParser;

    public HungerGameParser() {
        super(HungerGame.class);
    }

    @Override
    public Node define(Node source) {
        return source.firstChild("hunger", "food-level", "foodlevel");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.filterParser = context.type(Ref.class);
    }

    @Override
    protected ParserResult<HungerGame.Config> parseNode(Node node, String name, String value) throws ParserException {
        Ref<Filter> filter = this.filterParser.parse(node.property("filter")).orFail();

        return ParserResult.fine(node, name, value, new HungerGame.Config() {
            public Ref<Filter> filter() { return filter; }
        });
    }
}
