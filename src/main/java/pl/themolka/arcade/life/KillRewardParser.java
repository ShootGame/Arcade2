package pl.themolka.arcade.life;

import pl.themolka.arcade.config.ConfigParser;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.kit.Kit;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;

@Produces(KillReward.Config.class)
public class KillRewardParser extends ConfigParser<KillReward.Config>
                              implements InstallableParser {
    private Parser<Ref> filterParser;
    private Parser<Ref> kitParser;

    @Override
    public Set<Object> expect() {
        return Collections.singleton("kill reward");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.filterParser = context.type(Ref.class);
        this.kitParser = context.type(Ref.class);
    }

    @Override
    protected ParserResult<KillReward.Config> parsePrimitive(Node node, String name, String value) throws ParserException {
        String id = this.parseOptionalId(node);
        Ref<Filter> filter = this.filterParser.parse(node.property("filter")).orDefault(Ref.empty());
        Ref<Kit> kit = this.kitParser.parse(node).orFail();

        return ParserResult.fine(node, name, value, new KillReward.Config() {
            public String id() { return id; }
            public Ref<Filter> filter() { return filter; }
            public Ref<Kit> kit() { return kit; }
        });
    }
}
